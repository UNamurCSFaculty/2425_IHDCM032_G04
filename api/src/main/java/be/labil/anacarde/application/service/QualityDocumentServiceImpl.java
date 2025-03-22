package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.QualityDocumentDto;
import be.labil.anacarde.domain.mapper.QualityDocumentMapper;
import be.labil.anacarde.domain.model.QualityDocument;
import be.labil.anacarde.infrastructure.persistence.QualityDocumentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class QualityDocumentServiceImpl implements QualityDocumentService {

	private final QualityDocumentRepository qualityDocumentRepository;
	private final QualityDocumentMapper qualityDocumentMapper;

	@Override
	public QualityDocumentDto createQualityDocument(QualityDocumentDto qualityDocumentDto) {
		QualityDocument document = qualityDocumentMapper.toEntity(qualityDocumentDto);
		QualityDocument saved = qualityDocumentRepository.save(document);
		return qualityDocumentMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public QualityDocumentDto getQualityDocumentById(Integer id) {
		QualityDocument document = qualityDocumentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document de qualité non trouvé"));
		return qualityDocumentMapper.toDto(document);
	}

	@Override
	@Transactional(readOnly = true)
	public List<QualityDocumentDto> listQualityDocuments() {
		return qualityDocumentRepository.findAll().stream().map(qualityDocumentMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public QualityDocumentDto updateQualityDocument(Integer id, QualityDocumentDto qualityDocumentDto) {
		QualityDocument existingDocument = qualityDocumentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document de qualité non trouvé"));

		QualityDocument updatedDocument = qualityDocumentMapper.partialUpdate(qualityDocumentDto, existingDocument);
		QualityDocument saved = qualityDocumentRepository.save(updatedDocument);
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
