package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.CooperativeDto;
import be.labil.anacarde.domain.mapper.CooperativeMapper;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.infrastructure.persistence.CooperativeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class CooperativeServiceImpl implements CooperativeService {

	private final CooperativeRepository cooperativeRepository;
	private final CooperativeMapper cooperativeMapper;

	@Override
	public CooperativeDto createCooperative(CooperativeDto dto) {
		Cooperative cooperative = cooperativeMapper.toEntity(dto);
		Cooperative saved = cooperativeRepository.save(cooperative);
		return cooperativeMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public CooperativeDto getCooperativeById(Integer id) {
		Cooperative cooperative = cooperativeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coopérative non trouvée"));
		return cooperativeMapper.toDto(cooperative);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CooperativeDto> listCooperatives() {
		List<Cooperative> cooperatives = cooperativeRepository.findAll();
		return cooperatives.stream().map(cooperativeMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public CooperativeDto updateCooperative(Integer id, CooperativeDto dto) {
		Cooperative existing = cooperativeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coopérative non trouvée"));

		Cooperative updated = cooperativeMapper.partialUpdate(dto, existing);

		Cooperative saved = cooperativeRepository.save(updated);
		return cooperativeMapper.toDto(saved);
	}

	@Override
	public void deleteCooperative(Integer id) {
		if (!cooperativeRepository.existsById(id)) {
			throw new ResourceNotFoundException("Coopérative non trouvée");
		}
		cooperativeRepository.deleteById(id);
	}
}
