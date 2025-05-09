package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.QualityControlDto;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import be.labil.anacarde.domain.mapper.QualityControlMapper;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.infrastructure.persistence.QualityControlRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class QualityControlServiceImpl implements QualityControlService {

	private final QualityControlRepository qualityControlRepository;
	private final QualityControlMapper qualityControlMapper;
	private final PersistenceHelper persistenceHelper;

	@Override
	public QualityControlDto createQualityControl(QualityControlUpdateDto dto) {
		QualityControl entity = qualityControlMapper.toEntity(dto);

		QualityControl full = persistenceHelper.saveAndReload(qualityControlRepository, entity, QualityControl::getId);
		return qualityControlMapper.toDto(full);
	}

	@Override
	@Transactional(readOnly = true)
	public QualityControlDto getQualityControlById(Integer id) {

		QualityControl qc = qualityControlRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contrôle qualité non trouvé"));
		if (qc.getQualityInspector() != null && qc.getQualityInspector().getId() != null) {
			Hibernate.initialize(qc.getQualityInspector());
		}
		if (qc.getProduct() != null && qc.getProduct().getId() != null) {
			Hibernate.initialize(qc.getProduct());
		}
		if (qc.getDocument() != null && qc.getDocument().getId() != null) {
			Hibernate.initialize(qc.getDocument());
		}
		if (qc.getQuality() != null && qc.getQuality().getId() != null) {
			Hibernate.initialize(qc.getQuality());
		}
		return qualityControlMapper.toDto(qc);
	}

	@Override
	@Transactional(readOnly = true)
	public List<QualityControlDto> listQualityControls(Integer productId) {
		List<QualityControl> controls = qualityControlRepository.findByProductId(productId);
		return controls.stream().map(qualityControlMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public QualityControlDto updateQualityControl(Integer id, QualityControlUpdateDto dto) {
		QualityControl existing = qualityControlRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contrôle qualité non trouvé"));

		QualityControl updated = qualityControlMapper.partialUpdate(dto, existing);

		QualityControl full = persistenceHelper.saveAndReload(qualityControlRepository, updated, QualityControl::getId);
		return qualityControlMapper.toDto(full);
	}

	@Override
	public void deleteQualityControl(Integer id) {
		if (!qualityControlRepository.existsById(id)) {
			throw new ResourceNotFoundException("Contrôle qualité non trouvé");
		}
		qualityControlRepository.deleteById(id);
	}
}
