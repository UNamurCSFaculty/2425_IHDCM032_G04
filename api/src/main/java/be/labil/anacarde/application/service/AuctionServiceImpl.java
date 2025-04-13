package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.AuctionDto;
import be.labil.anacarde.domain.mapper.AuctionMapper;
import be.labil.anacarde.domain.model.Auction;
import be.labil.anacarde.infrastructure.persistence.AuctionRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AuctionServiceImpl implements AuctionService {
	private final AuctionRepository auctionRepository;
	private final AuctionMapper auctionMapper;

	@Override
	public AuctionDto createAuction(AuctionDto dto) {
		Auction Auction = auctionMapper.toEntity(dto);
		Auction saved = auctionRepository.save(Auction);
		return auctionMapper.toDto(saved);
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
	public List<AuctionDto> listAuctions() {
		return auctionRepository.findAll().stream().map(auctionMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public AuctionDto updateAuction(Integer id, AuctionDto auctionDetailDto) {
		Auction existingAuction = auctionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));

		Auction updatedAuction = auctionMapper.partialUpdate(auctionDetailDto, existingAuction);

		Auction saved = auctionRepository.save(updatedAuction);
		return auctionMapper.toDto(saved);
	}

	@Override
	public void deleteAuction(Integer id) {
		if (!auctionRepository.existsById(id)) {
			throw new ResourceNotFoundException("Enchère non trouvée");
		}
		auctionRepository.deleteById(id);
	}
}
