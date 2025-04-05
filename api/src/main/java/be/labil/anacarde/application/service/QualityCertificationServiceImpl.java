package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.QualityCertificationDto;
import be.labil.anacarde.domain.mapper.QualityCertificationMapper;
import be.labil.anacarde.domain.model.QualityCertification;
import be.labil.anacarde.infrastructure.persistence.QualityCertificationRepository;
import be.labil.anacarde.infrastructure.persistence.StoreRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class QualityCertificationServiceImpl implements QualityCertificationService {

	private final QualityCertificationRepository qualityCertificationRepository;
	private final QualityCertificationMapper qualityCertificationMapper;
	private final StoreRepository storeRepository;

	@Override
	public QualityCertificationDto createQualityCertification(QualityCertificationDto qualityCertificationDto) {
		if (!storeRepository.existsById(qualityCertificationDto.getStoreId())) {
			throw new ResourceNotFoundException("Magasin associé non trouvé");
		}
		QualityCertification certification = qualityCertificationMapper.toEntity(qualityCertificationDto);
		QualityCertification saved = qualityCertificationRepository.save(certification);
		return qualityCertificationMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public QualityCertificationDto getQualityCertificationById(Integer id) {
		QualityCertification certification = qualityCertificationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Certification non trouvée"));
		return qualityCertificationMapper.toDto(certification);
	}

	@Override
	@Transactional(readOnly = true)
	public List<QualityCertificationDto> listQualityCertifications() {
		return qualityCertificationRepository.findAll().stream().map(qualityCertificationMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public QualityCertificationDto updateQualityCertification(Integer id,
			QualityCertificationDto qualityCertificationDto) {
		QualityCertification existingCertification = qualityCertificationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Certification non trouvée"));

		QualityCertification updatedCertification = qualityCertificationMapper.partialUpdate(qualityCertificationDto,
				existingCertification);
		QualityCertification saved = qualityCertificationRepository.save(updatedCertification);
		return qualityCertificationMapper.toDto(saved);
	}

	@Override
	public void deleteQualityCertification(Integer id) {
		if (!qualityCertificationRepository.existsById(id)) {
			throw new ResourceNotFoundException("Certification non trouvée");
		}
		qualityCertificationRepository.deleteById(id);
	}
}
