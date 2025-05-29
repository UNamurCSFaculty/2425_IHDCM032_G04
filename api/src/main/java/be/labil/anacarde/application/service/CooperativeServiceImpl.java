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
import jakarta.persistence.EntityManager;
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
	private final EntityManager em;

	@Override
	public CooperativeDto createCooperative(CooperativeUpdateDto dto) {
		Cooperative coop = cooperativeMapper.toEntity(dto);

		if (dto.getPresidentId() != null) {
			Producer president = em.getReference(Producer.class, dto.getPresidentId());
			president.setCooperative(coop);
			coop.setPresident(president);
		} else if (coop.getPresident() != null) {
			coop.getPresident().setCooperative(coop);
		}

		Cooperative full = persistenceHelper.saveAndReload(cooperativeRepository, coop,
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
		return cooperativeRepository.findAll().stream().map(cooperativeMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public CooperativeDto updateCooperative(Integer id, CooperativeUpdateDto dto) {
		Cooperative coop = cooperativeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coopérative non trouvée"));

		cooperativeMapper.partialUpdate(dto, coop);

		if (dto.getPresidentId() != null) {
			Producer president = em.getReference(Producer.class, dto.getPresidentId());
			if (coop.getPresident() != null
					&& !coop.getPresident().getId().equals(dto.getPresidentId())) {
				coop.getPresident().setCooperative(null);
			}
			president.setCooperative(coop);
			coop.setPresident(president);
		} else if (coop.getPresident() != null) {
			coop.getPresident().setCooperative(null);
			coop.setPresident(null);
		}

		Cooperative saved = cooperativeRepository.save(coop);
		return cooperativeMapper.toDto(saved);
	}

	@Override
	public void deleteCooperative(Integer id) {
		Cooperative coop = cooperativeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coopérative non trouvée"));

		Producer president = coop.getPresident();
		if (president != null) {
			president.setCooperative(null);
			producerRepository.save(president);
		}

		List<Producer> producers = producerRepository.findByCooperativeId(coop.getId());
		for (Producer producer : producers) {
			producer.setCooperative(null);
			producerRepository.save(producer);
		}

		cooperativeRepository.delete(coop);
	}
}
