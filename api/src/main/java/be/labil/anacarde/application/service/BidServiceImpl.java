package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorException;
import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.BidDto;
import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.mapper.BidMapper;
import be.labil.anacarde.domain.model.Auction;
import be.labil.anacarde.domain.model.Bid;
import be.labil.anacarde.domain.model.TradeStatus;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import be.labil.anacarde.infrastructure.persistence.BidRepository;
import be.labil.anacarde.infrastructure.persistence.TradeStatusRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BidServiceImpl implements BidService {
	private static final Logger log = LoggerFactory.getLogger(BidServiceImpl.class);
	private final TradeStatusRepository tradeStatusRepository;
	private final BidRepository bidRepository;
	private final BidMapper bidMapper;
	private final PersistenceHelper persistenceHelper;
	private final GlobalSettingsService globalSettingsService;
	private final AuctionRepository auctionRepository;
	private final NotificationSseServiceImpl notificationSseService;
	private final AuctionSseServiceImpl auctionSseService;

	@Override
	public BidDto createBid(BidUpdateDto dto) {
		checkBidSettings(dto);
		checkAuctionHasAcceptedBid(dto.getAuctionId());

		Bid bid = bidMapper.toEntity(dto);

		if (dto.getStatusId() == null) {
			TradeStatus pendingStatus = tradeStatusRepository.findStatusPending();
			if (pendingStatus == null) {
				throw new ResourceNotFoundException("Status non trouvé");
			}
			bid.setStatus(pendingStatus);
		}

		Bid full = persistenceHelper.saveAndReload(bidRepository, bid, Bid::getId);
		BidDto bidDto = bidMapper.toDto(full);

		Auction auction = auctionRepository.findById(dto.getAuctionId())
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));
		// Ajoute l'enchérisseur à la liste des abonnés Redis (si pas déjà abonné)
		if (full.getTrader() != null && full.getTrader().getUsername() != null) {
			auctionSseService.addSubscriber(dto.getAuctionId(), full.getTrader().getUsername());
		}

		// Notifie tous les abonnés de la nouvelle enchère
		Set<String> subscribers = auctionSseService.getSubscribers(dto.getAuctionId());
		if (subscribers == null || subscribers.isEmpty()) {
			// fallback & notifie tous utilisateurs liés à l'enchère
			log.debug("[SSE] Aucun abonné trouvé, fallback & notification globale");
			List<Bid> allBids = bidRepository.findByAuctionId(dto.getAuctionId());
			subscribers = allBids.stream()
					.map(currentBid -> currentBid.getTrader() != null
							? currentBid.getTrader().getUsername()
							: null)
					.filter(Objects::nonNull).collect(Collectors.toSet());
			if (auction.getTrader() != null && auction.getTrader().getUsername() != null) {
				subscribers.add(auction.getTrader().getUsername());
			}
		}
		log.debug("[SSE] Liste d'abonnés à notifier pour l'enchère " + dto.getAuctionId()
				+ "(sauf auteur): " + subscribers);
		String author = full.getTrader() != null ? full.getTrader().getUsername() : null;
		for (String subKey : subscribers) {
			if (author != null && author.equals(subKey)) {
				continue;
			}
			notificationSseService.publishEvent(subKey, "newBid", bidDto);
			log.debug("[SSE] Notification envoyé à " + subKey + " pour nouvelle offre: " + bidDto);
		}

		Set<String> visitors = auctionSseService.getVisitors(dto.getAuctionId());
		log.debug("[SSE] Liste de visiteurs à notifier pour rafraîchissement silencieux: "
				+ visitors);
		if (visitors != null && !visitors.isEmpty()) {
			for (String visitorKey : visitors) {
				auctionSseService.sendEvent(dto.getAuctionId(), "refreshBids", bidDto);
				log.debug("[SSE] Notification silencieuse envoyée à " + visitorKey
						+ " pour refreshBids: " + bidDto);
			}
		}
		return bidDto;
	}

	@Override
	@Transactional(readOnly = true)
	public BidDto getBidById(Integer id) {
		Bid bid = bidRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));
		return bidMapper.toDto(bid);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BidDto> listBids(Integer auctionId) {
		List<Bid> bids;
		if (auctionId != null) {
			bids = bidRepository.findByAuctionId(auctionId);
		} else {
			bids = bidRepository.findAll();
		}
		return bids.stream().map(bidMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public BidDto updateBid(Integer id, BidUpdateDto bidDetailDto) {
		checkBidSettings(bidDetailDto);

		Bid existingBid = bidRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée"));

		Bid updatedBid = bidMapper.partialUpdate(bidDetailDto, existingBid);

		Bid full = persistenceHelper.saveAndReload(bidRepository, updatedBid, Bid::getId);
		return bidMapper.toDto(full);
	}

	@Override
	public BidDto acceptBid(Integer id) {
		TradeStatus acceptedStatus = tradeStatusRepository.findStatusAccepted();
		if (acceptedStatus == null) {
			throw new ResourceNotFoundException("Status non trouvé");
		}

		// Check another accepted bid does not exist
		Bid existingBid = bidRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée"));
		checkAuctionHasAcceptedBid(existingBid.getAuctionId());

		// Accept current bid
		existingBid.setStatus(acceptedStatus);

		Bid acceptedBid = bidRepository.save(existingBid);

		// Reject all other bids
		List<BidDto> otherBids = listBids(existingBid.getAuctionId());
		for (BidDto bidDto : otherBids) {
			if (bidDto.getId() != acceptedBid.getId()) {
				rejectBid(bidDto.getId());
			}
		}

		return bidMapper.toDto(acceptedBid);
	}

	@Override
	public BidDto rejectBid(Integer id) {
		TradeStatus rejectedStatus = tradeStatusRepository.findStatusRejected();
		if (rejectedStatus == null) {
			throw new ResourceNotFoundException("Status non trouvé");
		}

		Bid existingBid = bidRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée"));
		existingBid.setStatus(rejectedStatus);

		Bid saved = bidRepository.save(existingBid);
		return bidMapper.toDto(saved);
	}

	@Override
	public void deleteBid(Integer id) {
		if (!bidRepository.existsById(id)) {
			throw new ResourceNotFoundException("Offre non trouvée");
		}
		bidRepository.deleteById(id);
	}

	private void checkBidSettings(BidUpdateDto bidUpdateDto) {
		GlobalSettingsDto settings = globalSettingsService.getGlobalSettings();
		List<Bid> currentBids = null;

		if (settings.getForceBetterBids()) {
			currentBids = bidRepository.findByAuctionId(bidUpdateDto.getAuctionId());

			if (currentBids.size() > 0 && bidUpdateDto.getAmount().doubleValue() <= currentBids
					.getLast().getAmount().doubleValue()) {
				throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
						"forceBetterBids",
						"Une nouvelle offre doit être meilleure que la dernière");
			}
		}

		if (settings.getMinIncrement() != null && settings.getMinIncrement() > 0) {
			if (currentBids == null) {
				currentBids = bidRepository.findByAuctionId(bidUpdateDto.getAuctionId());
			}

			if (currentBids.size() > 0 && bidUpdateDto.getAmount().doubleValue() < currentBids
					.getLast().getAmount().doubleValue() + settings.getMinIncrement()) {
				throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
						"minIncrement", "Une nouvelle offre doit être meilleure de "
								+ settings.getMinIncrement() + " CFA que la dernière");
			}
		}
	}

	private void checkAuctionHasAcceptedBid(Integer auctionId) {
		TradeStatus acceptedStatus = tradeStatusRepository.findStatusAccepted();
		if (acceptedStatus == null) {
			throw new ResourceNotFoundException("Status non trouvé");
		}

		List<Bid> relatedBids = bidRepository.findByAuctionId(auctionId);
		if (relatedBids.stream().anyMatch(b -> b.getStatus().equals(acceptedStatus))) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
					"acceptBid", "Impossible d'accepter l'offre");
		}
	}
}
