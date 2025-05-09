package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.BidDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.mapper.BidMapper;
import be.labil.anacarde.domain.model.Bid;
import be.labil.anacarde.domain.model.TradeStatus;
import be.labil.anacarde.infrastructure.persistence.BidRepository;
import be.labil.anacarde.infrastructure.persistence.TradeStatusRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BidServiceImpl implements BidService {
	private final TradeStatusRepository tradeStatusRepository;
	private final BidRepository bidRepository;
	private final BidMapper bidMapper;
	private final PersistenceHelper persistenceHelper;

	@Override
	public BidDto createBid(BidUpdateDto dto) {
		Bid bid = bidMapper.toEntity(dto);
		Bid full = persistenceHelper.saveAndReload(bidRepository, bid, Bid::getId);
		return bidMapper.toDto(full);
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
		List<Bid> bids = bidRepository.findByAuctionId(auctionId);
		return bids.stream().map(bidMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public BidDto updateBid(Integer id, BidUpdateDto bidDetailDto) {
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

		Bid existingBid = bidRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée"));

		existingBid.setStatus(acceptedStatus);

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
}
