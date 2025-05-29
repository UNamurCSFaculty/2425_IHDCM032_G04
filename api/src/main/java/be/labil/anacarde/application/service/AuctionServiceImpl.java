package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.job.CloseAuctionJob;
import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.mapper.AuctionMapper;
import be.labil.anacarde.domain.model.Auction;
import be.labil.anacarde.domain.model.TradeStatus;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import be.labil.anacarde.infrastructure.persistence.TradeStatusRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private final AuctionSseService auctionSseService;
	private final NotificationSseService notificationSseService;

	private static final Logger log = LoggerFactory.getLogger(CloseAuctionJob.class);

	@Autowired
	private Scheduler scheduler;

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

		// Ajoute le vendeur comme abonné SSE à l'enchère
		if (full.getTrader() != null && full.getTrader().getUsername() != null) {
			auctionSseService.addSubscriber(full.getId(), full.getTrader().getUsername());
		}

		if (full.getExpirationDate() != null) {
			scheduleAuctionClose(Long.valueOf(full.getId()), full.getExpirationDate());
		}

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

		if (existingAuction.getStatus().getId()
				.equals(tradeStatusRepository.findStatusPending().getId())
				&& !existingAuction.getStatus().getId().equals(auctionDetailDto.getStatusId())) {
			deleteAuctionCloseJob(id.longValue());
		}
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
		if (existingAuction.getStatus().getId()
				.equals(tradeStatusRepository.findStatusPending().getId())) {
			deleteAuctionCloseJob(id.longValue());
		}
		existingAuction.setStatus(acceptedStatus);
		existingAuction.setExpirationDate(LocalDateTime.now());

		Auction saved = auctionRepository.save(existingAuction);
		notifyAuctionClosed(saved);
		return auctionMapper.toDto(saved);
	}

	@Override
	public void deleteAuction(Integer id) {
		if (auctionRepository.findById(id).isPresent()) {
			AuctionUpdateDto dto = new AuctionUpdateDto();
			dto.setActive(false);
			updateAuction(id, dto);
			deleteAuctionCloseJob(id.longValue());
		} else {
			throw new ResourceNotFoundException("Enchère non trouvée");
		}
	}

	public void deleteAuctionCloseJob(Long auctionId) {
		try {
			JobKey jobKey = new JobKey("auctionCloseJob-" + auctionId, "auction-jobs");
			if (scheduler.checkExists(jobKey)) {
				scheduler.deleteJob(jobKey);
				log.info("Job de clôture supprimé pour l'enchère ID : {}", auctionId);
			} else {
				log.warn(
						"Tentative de suppression d'un job de clôture non existant pour l'enchère ID : {}",
						auctionId);
			}
		} catch (SchedulerException e) {
			log.error(
					"Erreur lors de la suppression du job de clôture pour l'enchère ID : {}. Message : {}",
					auctionId, e.getMessage());
		}
	}

	@Transactional
	@Override
	public void closeAuction(Integer auctionId) {
		Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
		if (auctionOpt.isPresent()) {
			Auction auction = auctionOpt.get();
			if (auction.getStatus().getId()
					.equals(tradeStatusRepository.findStatusPending().getId())) {
				auction.setStatus(tradeStatusRepository.findStatusExpired());
				auctionRepository.save(auction);
				log.info("L'enchère ID {} a été marquée comme CLOSED.", auctionId);
				notifyAuctionClosed(auction);
				deleteAuctionCloseJob(auctionId.longValue());
			} else {
				log.warn(
						"Tentative de clôture d'une enchère ID {} qui n'est pas ACTIVE/PENDING. Statut actuel : {}",
						auctionId, auction.getStatus());
			}
		} else {
			log.warn("Tentative de clôture d'une enchère ID {} non trouvée.", auctionId);
		}
	}

	public void scheduleAuctionClose(Long auctionId, LocalDateTime endTime) {
		try {
			JobDetail jobDetail = JobBuilder.newJob(CloseAuctionJob.class)
					.withIdentity("auctionCloseJob-" + auctionId, "auction-jobs")
					.usingJobData("auctionId", auctionId).storeDurably().build();

			Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
					.withIdentity("auctionCloseTrigger-" + auctionId, "auction-triggers")
					.startAt(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()))
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withMisfireHandlingInstructionFireNow())
					.build();

			scheduler.scheduleJob(jobDetail, trigger);
			log.info("Job de clôture programmé pour l'enchère ID : {} à {}", auctionId, endTime);

		} catch (SchedulerException e) {
			log.error(
					"Erreur lors de la programmation du job de clôture pour l'enchère ID : {}. Message : {}",
					auctionId, e.getMessage());
		}
	}

	// Notifie les abonnés SSE de la clôture de l'enchère (acceptation ou expiration)
	private void notifyAuctionClosed(Auction auction) {
		Set<String> subscribers = auctionSseService.getSubscribers(auction.getId());
		AuctionDto auctionDto = auctionMapper.toDto(auction);
		if (subscribers != null && !subscribers.isEmpty()) {
			for (String subKey : subscribers) {
				notificationSseService.publishEvent(subKey, "auctionClosed", auctionDto);
				log.info("[SSE] Notification envoyée à " + subKey + " pour clôture de l'enchère: "
						+ auctionDto);
			}
		}
	}
}
