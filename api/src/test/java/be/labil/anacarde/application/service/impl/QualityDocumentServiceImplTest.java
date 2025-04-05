package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.QualityDocumentServiceImpl;
import be.labil.anacarde.domain.dto.QualityDocumentDto;
import be.labil.anacarde.domain.enums.Format;
import be.labil.anacarde.domain.mapper.QualityDocumentMapper;
import be.labil.anacarde.domain.model.QualityCertification;
import be.labil.anacarde.domain.model.QualityDocument;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.QualityCertificationRepository;
import be.labil.anacarde.infrastructure.persistence.QualityDocumentRepository;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
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
class QualityDocumentServiceImplTest {

	@Mock
	private QualityDocumentRepository qualityDocumentRepository;

	@Mock
	private QualityCertificationRepository qualityCertificationRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private QualityDocumentMapper qualitydocumentMapper;

	@InjectMocks
	private QualityDocumentServiceImpl qualityDocumentService;

	private QualityDocument qualityDocument;
	private QualityDocumentDto qualityDocumentDto;

	@BeforeEach
	void setUp() {
		qualityDocument = new QualityDocument();
		qualityDocument.setId(1);
		qualityDocument.setName("Rapport_Qualité_2025");
		qualityDocument.setFormat(Format.IMAGE);
		qualityDocument.setFilePath("/documents/2025/03/document.pdf");
		qualityDocument.setUploadDate(LocalDateTime.of(2025, 3, 13, 10, 15, 30));

		QualityDocument qualityDocument = new QualityDocument();
		qualityDocument.setId(3);
		QualityCertification qualityCertification = new QualityCertification();
		qualityDocument.setQualityCertification(qualityCertification);

		User user = new User();
		user.setId(1);
		qualityDocument.setUser(user);

		qualityDocumentDto = new QualityDocumentDto();
		qualityDocumentDto.setId(1);
		qualityDocumentDto.setName("Rapport_Qualité_2025");
		qualityDocumentDto.setFormat(Format.IMAGE);
		qualityDocumentDto.setFilePath("/documents/2025/03/document.pdf");
		qualityDocumentDto.setUploadDate(LocalDateTime.of(2025, 3, 13, 10, 15, 30));
		qualityDocumentDto.setQualityCertificationId(3);
		qualityDocumentDto.setUserId(1);

		lenient().when(qualitydocumentMapper.toEntity(any(QualityDocumentDto.class))).thenReturn(qualityDocument);
		lenient().when(qualitydocumentMapper.toDto(any(QualityDocument.class))).thenReturn(qualityDocumentDto);
	}

	@Test
	void testCreateQualityDocumentSuccess() {
		when(qualityDocumentRepository.save(any(QualityDocument.class))).thenReturn(qualityDocument);
		when(qualityCertificationRepository.existsById(any(Integer.class))).thenReturn(true);
		when(userRepository.existsById(any(Integer.class))).thenReturn(true);
		QualityDocumentDto result = qualityDocumentService.createQualityDocument(qualityDocumentDto);
		assertThat(result).isEqualTo(qualityDocumentDto);
		verify(qualityDocumentRepository, times(1)).save(any(QualityDocument.class));
	}

	@Test
	void testGetQualityDocumentByIdSuccess() {
		when(qualityDocumentRepository.findById(1)).thenReturn(Optional.of(qualityDocument));
		QualityDocumentDto result = qualityDocumentService.getQualityDocumentById(1);
		assertThat(result).isEqualTo(qualityDocumentDto);
	}

	@Test
	void testGetQualityDocumentByIdNotFound() {
		when(qualityDocumentRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> qualityDocumentService.getQualityDocumentById(1))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Document de qualité non trouvé");
	}

	@Test
	void testListQualityDocuments() {
		when(qualityDocumentRepository.findAll()).thenReturn(Collections.singletonList(qualityDocument));
		List<QualityDocumentDto> results = qualityDocumentService.listQualityDocuments();
		assertThat(results).hasSize(1).contains(qualityDocumentDto);
	}

	@Test
	void testUpdateQualityDocument() {
		when(qualityDocumentRepository.findById(1)).thenReturn(Optional.of(qualityDocument));
		when(qualityDocumentRepository.save(any(QualityDocument.class))).thenReturn(qualityDocument);
		when(qualitydocumentMapper.partialUpdate(any(QualityDocumentDto.class), any(QualityDocument.class)))
				.thenReturn(qualityDocument);
		QualityDocumentDto result = qualityDocumentService.updateQualityDocument(1, qualityDocumentDto);
		assertThat(result).isEqualTo(qualityDocumentDto);
	}

	@Test
	void testDeleteQualityDocumentSuccess() {
		when(qualityDocumentRepository.existsById(1)).thenReturn(true);
		qualityDocumentService.deleteQualityDocument(1);
		verify(qualityDocumentRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeleteQualityCertificationNotFound() {
		when(qualityDocumentRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> qualityDocumentService.deleteQualityDocument(1))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Document de qualité non trouvé");
	}
}
