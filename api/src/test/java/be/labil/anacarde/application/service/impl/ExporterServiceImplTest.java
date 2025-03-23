package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.ExporterServiceImpl;
import be.labil.anacarde.domain.dto.ExporterDto;
import be.labil.anacarde.domain.mapper.ExporterMapper;
import be.labil.anacarde.domain.model.Exporter;
import be.labil.anacarde.infrastructure.persistence.ExporterRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExporterServiceImplTest {

	@Mock
	private ExporterRepository exporterRepository;

	@Mock
	private ExporterMapper exporterMapper;

	@InjectMocks
	private ExporterServiceImpl exporterService;

	private Exporter exporter;
	private ExporterDto exporterDto;

	@BeforeEach
	void setUp() {
		exporter = new Exporter();
		exporter.setId(1);

		exporterDto = new ExporterDto();
		exporterDto.setId(1);

		Mockito.lenient().when(exporterMapper.toEntity(any(ExporterDto.class))).thenReturn(exporter);
		Mockito.lenient().when(exporterMapper.toDto(any(Exporter.class))).thenReturn(exporterDto);
	}

	@Test
	void testCreateExporter() {
		when(exporterRepository.save(exporter)).thenReturn(exporter);
		ExporterDto result = exporterService.createExporter(exporterDto);
		assertThat(result).isEqualTo(exporterDto);
		verify(exporterRepository, times(1)).save(exporter);
	}

	@Test
	void testGetExporterByIdSuccess() {
		when(exporterRepository.findById(1)).thenReturn(Optional.of(exporter));
		ExporterDto result = exporterService.getExporterById(1);
		assertThat(result).isEqualTo(exporterDto);
	}

	@Test
	void testGetExporterByIdNotFound() {
		when(exporterRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> exporterService.getExporterById(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Exportateur non trouvé");
	}

	@Test
	void testListExporters() {
		when(exporterRepository.findAll()).thenReturn(Collections.singletonList(exporter));
		List<ExporterDto> result = exporterService.listExporters();
		assertThat(result).hasSize(1).contains(exporterDto);
	}

	@Test
	void testUpdateExporterSuccess() {
		ExporterDto updatedDto = new ExporterDto();

		when(exporterRepository.findById(1)).thenReturn(Optional.of(exporter));
		when(exporterMapper.partialUpdate(any(ExporterDto.class), any(Exporter.class))).thenAnswer(invocation -> {
			Exporter existing = invocation.getArgument(1);
			return existing;
		});
		when(exporterRepository.save(exporter)).thenReturn(exporter);
		when(exporterMapper.toDto(exporter)).thenReturn(updatedDto);

		ExporterDto result = exporterService.updateExporter(1, updatedDto);
		assertThat(result.getBidderId()).isEqualTo(updatedDto.getBidderId());
	}

	@Test
	void testDeleteExporterSuccess() {
		when(exporterRepository.existsById(1)).thenReturn(true);
		exporterService.deleteExporter(1);
		verify(exporterRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeleteExporterNotFound() {
		when(exporterRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> exporterService.deleteExporter(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Exportateur non trouvé");
	}
}
