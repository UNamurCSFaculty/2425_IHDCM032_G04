package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.CooperativeDto;
import be.labil.anacarde.domain.dto.write.CooperativeUpdateDto;
import be.labil.anacarde.domain.mapper.CooperativeMapper;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.infrastructure.persistence.CooperativeRepository;
import be.labil.anacarde.infrastructure.persistence.user.ProducerRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
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
	private final ProducerRepository producerRepository;
	private final PersistenceHelper persistenceHelper;

	@Override
	public CooperativeDto createCooperative(CooperativeUpdateDto dto) {
		Cooperative cooperative = cooperativeMapper.toEntity(dto);
		Cooperative full = persistenceHelper.saveAndReload(cooperativeRepository, cooperative,
				Cooperative::getId);
		return cooperativeMapper.toDto(full);
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
	public CooperativeDto updateCooperative(Integer id, CooperativeUpdateDto dto) {
		Cooperative existing = cooperativeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coopérative non trouvée"));

		Cooperative updated = cooperativeMapper.partialUpdate(dto, existing);

		Cooperative full = persistenceHelper.saveAndReload(cooperativeRepository, updated,
				Cooperative::getId);
		return cooperativeMapper.toDto(full);
	}

	/**
	 * @Override public void deleteCooperative(Integer id) { if
	 *           (!cooperativeRepository.existsById(id)) { throw new
	 *           ResourceNotFoundException("Coopérative non trouvée"); }
	 *           cooperativeRepository.deleteById(id); }
	 */

	@Override
	public void deleteCooperative(Integer id) {
		Cooperative coop = cooperativeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coopérative non trouvée"));

		Producer president = coop.getPresident();
		if (president != null) {
			president.setCooperative(null);
			producerRepository.save(president);
		}

		cooperativeRepository.delete(coop);
	}
}
