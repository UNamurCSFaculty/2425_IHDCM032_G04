package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.QualityControlDto;
import be.labil.anacarde.domain.mapper.QualityControlMapper;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.infrastructure.persistence.QualityControlRepository;
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

	@Override
	public QualityControlDto createQualityControl(QualityControlDto dto) {
		QualityControl entity = qualityControlMapper.toEntity(dto);
		QualityControl saved = qualityControlRepository.save(entity);
		return qualityControlMapper.toDto(saved);
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
		QualityControlDto qualityControlDto = qualityControlMapper.toDto(qc);
		return qualityControlDto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<QualityControlDto> listQualityControls(Integer productId) {
		List<QualityControl> controls = qualityControlRepository.findByProductId(productId);
		return controls.stream().map(qualityControlMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public QualityControlDto updateQualityControl(Integer id, QualityControlDto dto) {
		QualityControl existing = qualityControlRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contrôle qualité non trouvé"));

		QualityControl updated = qualityControlMapper.partialUpdate(dto, existing);

		QualityControl saved = qualityControlRepository.save(updated);
		return qualityControlMapper.toDto(saved);
	}

	@Override
	public void deleteQualityControl(Integer id) {
		if (!qualityControlRepository.existsById(id)) {
			throw new ResourceNotFoundException("Contrôle qualité non trouvé");
		}
		qualityControlRepository.deleteById(id);
	}
}
