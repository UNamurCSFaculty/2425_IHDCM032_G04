package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.DocumentServiceImpl;
import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.enums.Format;
import be.labil.anacarde.domain.mapper.DocumentMapper;
import be.labil.anacarde.domain.model.QualityCertification;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

	@Mock
	private DocumentRepository documentRepository;

	@Mock
	private DocumentMapper documentMapper;

	@InjectMocks
	private DocumentServiceImpl documentService;

	private Document document;
	private DocumentDto documentDto;

	@BeforeEach
	void setUp() {
		document = new Document();
		document.setId(1);
		document.setName("Rapport_Qualité_2025");
		document.setFormat(Format.IMAGE);
		document.setFilePath("/documents/2025/03/document.pdf");
		document.setUploadDate(LocalDateTime.of(2025, 3, 13, 10, 15, 30));

		Document document = new Document();
		document.setId(3);
		QualityCertification qualityCertification = new QualityCertification();
		document.setQualityCertification(qualityCertification);

		User user = new User();
		user.setId(1);
		document.setUser(user);

		documentDto = new DocumentDto();
		documentDto.setId(1);
		documentDto.setName("Rapport_Qualité_2025");
		documentDto.setFormat(Format.IMAGE);
		documentDto.setFilePath("/documents/2025/03/document.pdf");
		documentDto.setUploadDate(LocalDateTime.of(2025, 3, 13, 10, 15, 30));
		documentDto.setQualityCertificationId(3);
		documentDto.setUserId(1);

		lenient().when(documentMapper.toEntity(any(DocumentDto.class))).thenReturn(document);
		lenient().when(documentMapper.toDto(any(Document.class))).thenReturn(documentDto);
	}

	@Test
	void testcreateDocument() {
		when(documentRepository.save(any(Document.class))).thenReturn(document);
		DocumentDto result = documentService.createDocument(documentDto);
		assertThat(result).isEqualTo(documentDto);
		verify(documentRepository, times(1)).save(any(Document.class));
	}

	@Test
	void testGetDocumentByIdSuccess() {
		when(documentRepository.findById(1)).thenReturn(Optional.of(document));
		DocumentDto result = documentService.getDocumentById(1);
		assertThat(result).isEqualTo(documentDto);
	}

	@Test
	void testGetDocumentByIdNotFound() {
		when(documentRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> documentService.getDocumentById(1))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Document de qualité non trouvé");
	}

	@Test
	void testListDocuments() {
		when(documentRepository.findAll()).thenReturn(Collections.singletonList(document));
		List<DocumentDto> results = documentService.listDocuments();
		assertThat(results).hasSize(1).contains(documentDto);
	}

	@Test
	void testUpdateDocument() {
		when(documentRepository.findById(1)).thenReturn(Optional.of(document));
		when(documentRepository.save(any(Document.class))).thenReturn(document);
		when(documentMapper.partialUpdate(any(DocumentDto.class), any(Document.class)))
				.thenReturn(document);
		DocumentDto result = documentService.updateDocument(1, documentDto);
		assertThat(result).isEqualTo(documentDto);
	}

	@Test
	void testDeleteDocumentSuccess() {
		when(documentRepository.existsById(1)).thenReturn(true);
		documentService.deleteDocument(1);
		verify(documentRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeleteQualityCertificationNotFound() {
		when(documentRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> documentService.deleteDocument(1))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Document de qualité non trouvé");
	}
}
