package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.BidDto;
import be.labil.anacarde.domain.mapper.BidMapper;
import be.labil.anacarde.domain.model.Bid;
import be.labil.anacarde.infrastructure.persistence.BidRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BidServiceImpl implements BidService {
	private final BidRepository bidRepository;
	private final BidMapper bidMapper;

	@Override
	public BidDto createBid(BidDto dto) {
		Bid bid = bidMapper.toEntity(dto);
		Bid saved = bidRepository.save(bid);
		return bidMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public BidDto getBidById(Integer id) {
		Bid Bid = bidRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));
		return bidMapper.toDto(Bid);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BidDto> listBids(Integer auctionId) {
		List<Bid> bids = bidRepository.findByAuctionId(auctionId);
		return bids.stream().map(bidMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public BidDto updateBid(Integer id, BidDto bidDetailDto) {
		Bid existingBid = bidRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée"));

		Bid updatedBid = bidMapper.partialUpdate(bidDetailDto, existingBid);

		Bid saved = bidRepository.save(updatedBid);
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
