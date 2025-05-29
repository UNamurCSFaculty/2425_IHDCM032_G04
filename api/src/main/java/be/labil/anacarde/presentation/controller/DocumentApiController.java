package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.DocumentService;
import be.labil.anacarde.domain.dto.db.DocumentDto;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Implémentation de {@link DocumentApi}.
 */
@RestController
@RequiredArgsConstructor
public class DocumentApiController implements DocumentApi {

	private final DocumentService documentService;

	@Override
	public ResponseEntity<Resource> downloadDocument(Integer id) {
		// on récupère le flux binaire en une seule méthode
		InputStream in = documentService.streamDocumentContent(id);
		InputStreamResource resource = new InputStreamResource(in);

		// retrouvez le content-type et le nom d’origine via getDocumentById
		DocumentDto meta = documentService.getDocumentById(id);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(meta.getContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + meta.getOriginalFilename() + "\"")
				.body(resource);
	}

	@Override
	public ResponseEntity<DocumentDto> getDocument(Integer id) {
		DocumentDto dto = documentService.getDocumentById(id);
		return ResponseEntity.ok(dto);
	}

	@Override
	public ResponseEntity<DocumentDto> createDocumentUser(Integer userId, MultipartFile file) {
		DocumentDto created = documentService.createDocumentUser(userId, file);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<DocumentDto> createDocumentQualityControl(Integer qualityControlId,
			MultipartFile file) {
		DocumentDto created = documentService.createDocumentQualityControl(qualityControlId, file);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<Void> deleteDocument(Integer id) {
		documentService.deleteDocument(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<List<DocumentDto>> listDocumentsByUser(Integer userId) {
		List<DocumentDto> list = documentService.listDocumentsByUser(userId);
		return ResponseEntity.ok(list);
	}
}
