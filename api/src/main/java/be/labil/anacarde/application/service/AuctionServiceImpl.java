package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.mapper.AuctionMapper;
import be.labil.anacarde.domain.model.Auction;
import be.labil.anacarde.domain.model.TradeStatus;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import be.labil.anacarde.infrastructure.persistence.TradeStatusRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AuctionServiceImpl implements AuctionService {
	private final TradeStatusRepository tradeStatusRepository;
	private final AuctionRepository auctionRepository;
	private final AuctionMapper auctionMapper;
	private final PersistenceHelper persistenceHelper;

	@Override
	public AuctionDto createAuction(AuctionUpdateDto dto) {
		Auction auction = auctionMapper.toEntity(dto);

		if (dto.getStatusId() == null) {
			TradeStatus pendingStatus = tradeStatusRepository.findStatusPending();
			if (pendingStatus == null) {
				throw new ResourceNotFoundException("Status non trouvé");
			}
			auction.setStatus(pendingStatus);
		}

		Auction full = persistenceHelper.saveAndReload(auctionRepository, auction, Auction::getId);
		return auctionMapper.toDto(full);
	}

	@Override
	@Transactional(readOnly = true)
	public AuctionDto getAuctionById(Integer id) {
		Auction Auction = auctionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));
		return auctionMapper.toDto(Auction);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AuctionDto> listAuctions(Integer traderId, Integer buyerId, String status) {
		if (traderId != null && buyerId != null) {
			throw new IllegalArgumentException(
					"TraderId et BuyerId ne peuvent pas être spécifiés en même temps.");
		} else if (buyerId != null) {
			return auctionRepository.findByBuyerAndStatus(buyerId, status).stream()
					.map(auctionMapper::toDto).collect(Collectors.toList());
		} else {
			return auctionRepository.findByTraderAndStatus(traderId, status).stream()
					.map(auctionMapper::toDto).collect(Collectors.toList());
		}
	}

	@Override
	public AuctionDto updateAuction(Integer id, AuctionUpdateDto auctionDetailDto) {
		Auction existingAuction = auctionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));

		Auction updatedAuction = auctionMapper.partialUpdate(auctionDetailDto, existingAuction);

		Auction full = persistenceHelper.saveAndReload(auctionRepository, updatedAuction,
				Auction::getId);
		return auctionMapper.toDto(full);
	}

	@Override
	public AuctionDto acceptAuction(Integer id) {
		TradeStatus acceptedStatus = tradeStatusRepository.findStatusAccepted();
		if (acceptedStatus == null) {
			throw new ResourceNotFoundException("Status non trouvé");
		}

		Auction existingAuction = auctionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));
		existingAuction.setStatus(acceptedStatus);
		existingAuction.setExpirationDate(LocalDateTime.now());

		Auction saved = auctionRepository.save(existingAuction);
		return auctionMapper.toDto(saved);
	}

	@Override
	public void deleteAuction(Integer id) {
		if (auctionRepository.findById(id).isPresent()) {
			AuctionUpdateDto dto = new AuctionUpdateDto();
			dto.setActive(false);
			updateAuction(id, dto);
		} else {
			throw new ResourceNotFoundException("Enchère non trouvée");
		}
	}
}
