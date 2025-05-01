package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.DocumentService;
import be.labil.anacarde.domain.dto.DocumentDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class DocumentApiController implements DocumentApi {

	private final DocumentService documentService;

	@Override
	public ResponseEntity<DocumentDto> getDocument(Integer id) {
		DocumentDto document = documentService.getDocumentById(id);
		return ResponseEntity.ok(document);
	}

	@Override
	public ResponseEntity<DocumentDto> createDocument(DocumentDto documentDto) {
		DocumentDto created = documentService.createDocument(documentDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<DocumentDto> updateDocument(Integer id, DocumentDto documentDto) {
		DocumentDto updated = documentService.updateDocument(id, documentDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteDocument(Integer id) {
		documentService.deleteDocument(id);
		return ResponseEntity.noContent().build();
	}

	// @Override
	// public ResponseEntity<List<DocumentDto>> listDocumentsByQualityControl(Integer qualityControlId) {
	// List<DocumentDto> documents = documentService.listDocumentsByQualityControl(qualityControlId);
	// return ResponseEntity.ok(documents);
	// }

	@Override
	public ResponseEntity<List<DocumentDto>> listDocumentsByUser(Integer userId) {
		List<DocumentDto> documents = documentService.listDocumentsByUser(userId);
		return ResponseEntity.ok(documents);
	}
}
