package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.AuctionDto;
import be.labil.anacarde.domain.dto.AuctionUpdateDto;
import be.labil.anacarde.domain.mapper.AuctionMapper;
import be.labil.anacarde.domain.model.Auction;
import be.labil.anacarde.domain.model.TradeStatus;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import be.labil.anacarde.infrastructure.persistence.TradeStatusRepository;
import jakarta.persistence.EntityManager;
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
	private final EntityManager entityManager;

	@Override
	public AuctionDto createAuction(AuctionUpdateDto dto) {
		Auction auction = auctionMapper.fromUpdateDto(dto);

		if (dto.getStatusId() == null) {
			TradeStatus pendingStatus = tradeStatusRepository.findStatusPending();
			if (pendingStatus == null) {
				throw new ResourceNotFoundException("Status non trouvé");
			}
			auction.setStatus(pendingStatus);
		}

		Auction saved = auctionRepository.save(auction);
		// On flush pour s'assurer que l'entité est bien persisté
		// On clear pour éviter les problèmes de lazy loading (cache Hibernate)
		// On recharge l'entité pour s'assurer que toutes les relations sont bien
		entityManager.flush();
		entityManager.clear();
		Auction full = auctionRepository.findById(saved.getId()).orElseThrow(() -> new ResourceNotFoundException(
				"Création échouée : impossible de recharger l’enchère id=" + saved.getId()));
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
	public List<AuctionDto> listAuctions(Integer traderId, String status) {
		return auctionRepository.findByActiveTrueFiltered(traderId, status).stream().map(auctionMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public AuctionDto updateAuction(Integer id, AuctionUpdateDto auctionDetailDto) {
		Auction existingAuction = auctionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));

		Auction updatedAuction = auctionMapper.partialUpdateFromDto(auctionDetailDto, existingAuction);

		Auction saved = auctionRepository.save(updatedAuction);
		// On flush pour s'assurer que l'entité est bien persisté
		// On clear pour éviter les problèmes de lazy loading (cache Hibernate)
		// On recharge l'entité pour s'assurer que toutes les relations sont bien
		entityManager.flush();
		entityManager.clear();
		Auction full = auctionRepository.findById(saved.getId()).orElseThrow(() -> new ResourceNotFoundException(
				"Création échouée : impossible de recharger l’enchère id=" + saved.getId()));
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
