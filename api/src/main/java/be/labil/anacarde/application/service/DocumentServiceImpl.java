package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.DocumentDto;
import be.labil.anacarde.domain.mapper.DocumentMapper;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

	private final DocumentRepository documentRepository;
	private final DocumentMapper documentMapper;

	@Override
	public DocumentDto createDocument(DocumentDto dto) {
		Document document = documentMapper.toEntity(dto);
		Document saved = documentRepository.save(document);
		return documentMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto getDocumentById(Integer id) {
		Document document = documentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));
		return documentMapper.toDto(document);
	}

	@Override
	public DocumentDto updateDocument(Integer id, DocumentDto dto) {
		Document existing = documentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));

		Document updated = documentMapper.partialUpdate(dto, existing);
		Document saved = documentRepository.save(updated);
		return documentMapper.toDto(saved);
	}

	@Override
	public void deleteDocument(Integer id) {
		if (!documentRepository.existsById(id)) {
			throw new ResourceNotFoundException("Document non trouvé");
		}
		documentRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> listDocumentsByUser(Integer userId) {
		return documentRepository.findByUserId(userId).stream().map(documentMapper::toDto).collect(Collectors.toList());
	}
}
