package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.AuctionStrategyDto;
import be.labil.anacarde.domain.mapper.AuctionStrategyMapper;
import be.labil.anacarde.domain.model.AuctionStrategy;
import be.labil.anacarde.infrastructure.persistence.AuctionStrategyRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionStrategyServiceImpl implements AuctionStrategyService {

	private final AuctionStrategyRepository repository;
	private final AuctionStrategyMapper mapper;

	@Override
	public AuctionStrategyDto createAuctionStrategy(AuctionStrategyDto dto) {
		AuctionStrategy entity = mapper.toEntity(dto);
		AuctionStrategy saved = repository.save(entity);
		return mapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public AuctionStrategyDto getAuctionStrategyById(Integer id) {
		AuctionStrategy entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stratégie d'enchère non trouvée"));
		return mapper.toDto(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AuctionStrategyDto> listAuctionStrategies() {
		return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
	}

	@Override
	public AuctionStrategyDto updateAuctionStrategy(Integer id, AuctionStrategyDto dto) {
		AuctionStrategy existing = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stratégie d'enchère non trouvée"));

		AuctionStrategy updated = mapper.partialUpdate(dto, existing);
		AuctionStrategy saved = repository.save(updated);
		return mapper.toDto(saved);
	}

	@Override
	public void deleteAuctionStrategy(Integer id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Stratégie d'enchère non trouvée");
		}
		repository.deleteById(id);
	}
}
