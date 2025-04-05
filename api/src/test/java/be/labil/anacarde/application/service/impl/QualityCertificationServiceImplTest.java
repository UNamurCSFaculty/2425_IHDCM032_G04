package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.QualityCertificationServiceImpl;
import be.labil.anacarde.domain.dto.QualityCertificationDto;
import be.labil.anacarde.domain.mapper.QualityCertificationMapper;
import be.labil.anacarde.domain.model.QualityCertification;
import be.labil.anacarde.domain.model.Store;
import be.labil.anacarde.infrastructure.persistence.QualityCertificationRepository;
import be.labil.anacarde.infrastructure.persistence.StoreRepository;
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
public class QualityCertificationServiceImplTest {

	@Mock
	private QualityCertificationRepository qualityCertificationRepository;

	@Mock
	private QualityCertificationMapper qualityCertificationMapper;

	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private QualityCertificationServiceImpl qualityCertificationService;

	private QualityCertification qualityCertification;
	private QualityCertificationDto qualityCertificationDto;

	@BeforeEach
	void setUp() {
		qualityCertification = new QualityCertification();
		qualityCertification.setId(1);
		Store store = new Store();
		store.setId(1);
		qualityCertification.setStore(store);

		qualityCertificationDto = new QualityCertificationDto();
		qualityCertificationDto.setId(1);
		qualityCertificationDto.setStoreId(1);

		lenient().when(qualityCertificationMapper.toEntity(any(QualityCertificationDto.class)))
				.thenReturn(qualityCertification);
		lenient().when(qualityCertificationMapper.toDto(any(QualityCertification.class)))
				.thenReturn(qualityCertificationDto);
	}

	@Test
	void testCreateQualityCertification() {
		when(qualityCertificationRepository.save(any(QualityCertification.class))).thenReturn(qualityCertification);
		when(storeRepository.existsById(any(Integer.class))).thenReturn(true);
		QualityCertificationDto result = qualityCertificationService
				.createQualityCertification(qualityCertificationDto);
		assertThat(result).isEqualTo(qualityCertificationDto);
		verify(qualityCertificationRepository, times(1)).save(any(QualityCertification.class));
	}

	@Test
	void testGetQualityCertificationByIdSuccess() {
		when(qualityCertificationRepository.findById(1)).thenReturn(Optional.of(qualityCertification));
		QualityCertificationDto result = qualityCertificationService.getQualityCertificationById(1);
		assertThat(result).isEqualTo(qualityCertificationDto);
	}

	@Test
	void testGetQualityCertificationByIdNotFound() {
		when(qualityCertificationRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> qualityCertificationService.getQualityCertificationById(1))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Certification non trouvée");
	}

	@Test
	void testListQualityCertifications() {
		when(qualityCertificationRepository.findAll()).thenReturn(Collections.singletonList(qualityCertification));
		List<QualityCertificationDto> results = qualityCertificationService.listQualityCertifications();
		assertThat(results).hasSize(1).contains(qualityCertificationDto);
	}

	@Test
	void testUpdateQualityCertification() {
		when(qualityCertificationRepository.findById(1)).thenReturn(Optional.of(qualityCertification));
		when(qualityCertificationRepository.save(any(QualityCertification.class))).thenReturn(qualityCertification);
		when(qualityCertificationMapper.partialUpdate(any(QualityCertificationDto.class),
				any(QualityCertification.class))).thenReturn(qualityCertification);
		QualityCertificationDto result = qualityCertificationService.updateQualityCertification(1,
				qualityCertificationDto);
		assertThat(result).isEqualTo(qualityCertificationDto);
	}

	@Test
	void testDeleteQualityCertificationSuccess() {
		when(qualityCertificationRepository.existsById(1)).thenReturn(true);
		qualityCertificationService.deleteQualityCertification(1);
		verify(qualityCertificationRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeleteQualityCertificationNotFound() {
		when(qualityCertificationRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> qualityCertificationService.deleteQualityCertification(1))
				.isInstanceOf(ResourceNotFoundException.class).hasMessage("Certification non trouvée");
	}
}
