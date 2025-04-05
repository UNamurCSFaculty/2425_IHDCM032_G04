package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.QualityDocumentDto;
import be.labil.anacarde.domain.mapper.QualityDocumentMapper;
import be.labil.anacarde.domain.model.QualityDocument;
import be.labil.anacarde.infrastructure.persistence.QualityCertificationRepository;
import be.labil.anacarde.infrastructure.persistence.QualityDocumentRepository;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class QualityDocumentServiceImpl implements QualityDocumentService {

	private final QualityDocumentRepository qualityDocumentRepository;
	private final QualityDocumentMapper qualityDocumentMapper;
	private final UserRepository userRepository;

	private final QualityCertificationRepository qualityCertificationRepository;

	@Override
	public QualityDocumentDto createQualityDocument(QualityDocumentDto qualityDocumentDto) {
		if (!qualityCertificationRepository.existsById(qualityDocumentDto.getQualityCertificationId())) {
			throw new ResourceNotFoundException("Certification de qualité non trouvée");
		}
		if (!userRepository.existsById(qualityDocumentDto.getUserId())) {
			throw new ResourceNotFoundException("Utilisateur associé non trouvé");
		}
		QualityDocument qualityDocument = qualityDocumentMapper.toEntity(qualityDocumentDto);
		QualityDocument saved = qualityDocumentRepository.save(qualityDocument);
		return qualityDocumentMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public QualityDocumentDto getQualityDocumentById(Integer id) {
		QualityDocument qualityDocument = qualityDocumentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document de qualité non trouvé"));
		return qualityDocumentMapper.toDto(qualityDocument);
	}

	@Override
	@Transactional(readOnly = true)
	public List<QualityDocumentDto> listQualityDocuments() {
		return qualityDocumentRepository.findAll().stream().map(qualityDocumentMapper::toDto).toList();
	}

	@Override
	public QualityDocumentDto updateQualityDocument(Integer id, QualityDocumentDto qualityDocumentDto) {
		QualityDocument existingQualityDocument = qualityDocumentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document de qualité non trouvé"));

		QualityDocument updatedQualityDocument = qualityDocumentMapper.partialUpdate(qualityDocumentDto,
				existingQualityDocument);
		QualityDocument saved = qualityDocumentRepository.save(updatedQualityDocument);
		return qualityDocumentMapper.toDto(saved);
	}

	@Override
	public void deleteQualityDocument(Integer id) {
		if (!qualityDocumentRepository.existsById(id)) {
			throw new ResourceNotFoundException("Document de qualité non trouvé");
		}
		qualityDocumentRepository.deleteById(id);
	}
}
