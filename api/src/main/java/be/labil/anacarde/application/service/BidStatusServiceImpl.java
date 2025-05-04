package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.BidStatusDto;
import be.labil.anacarde.domain.mapper.BidStatusMapper;
import be.labil.anacarde.domain.model.BidStatus;
import be.labil.anacarde.infrastructure.persistence.BidStatusRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BidStatusServiceImpl implements BidStatusService {
	private final BidStatusRepository bidStatusRepository;
	private final BidStatusMapper bidStatusMapper;

	@Override
	public BidStatusDto createBidStatus(BidStatusDto dto) {
		BidStatus bidStatus = bidStatusMapper.toEntity(dto);
		BidStatus saved = bidStatusRepository.save(bidStatus);
		return bidStatusMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public BidStatusDto getBidStatusById(Integer id) {
		BidStatus BidStatus = bidStatusRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));
		return bidStatusMapper.toDto(BidStatus);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BidStatusDto> listBidStatus() {
		List<BidStatus> bidStatus = bidStatusRepository.findAll();
		return bidStatus.stream().map(bidStatusMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public BidStatusDto updateBidStatus(Integer id, BidStatusDto bidStatusDetailDto) {
		BidStatus existingBidStatus = bidStatusRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Status non trouvé"));

		BidStatus updatedBidStatus = bidStatusMapper.partialUpdate(bidStatusDetailDto, existingBidStatus);

		BidStatus saved = bidStatusRepository.save(updatedBidStatus);
		return bidStatusMapper.toDto(saved);
	}

	@Override
	public void deleteBidStatus(Integer id) {
		if (!bidStatusRepository.existsById(id)) {
			throw new ResourceNotFoundException("Status non trouvé");
		}
		bidStatusRepository.deleteById(id);
	}
}
