package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.ExporterDto;
import be.labil.anacarde.domain.mapper.ExporterMapper;
import be.labil.anacarde.domain.model.Exporter;
import be.labil.anacarde.infrastructure.persistence.ExporterRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
/**
 * Ce service gère les opérations relatives aux exportateurs.
 */
public class ExporterServiceImpl implements ExporterService {

	private final ExporterRepository exporterRepository;
	private final ExporterMapper exporterMapper;

	@Override
	public ExporterDto createExporter(ExporterDto exporterDto) {
		Exporter exporter = exporterMapper.toEntity(exporterDto);
		Exporter saved = exporterRepository.save(exporter);
		return exporterMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public ExporterDto getExporterById(Integer id) {
		Exporter exporter = exporterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exportateur non trouvé"));
		return exporterMapper.toDto(exporter);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExporterDto> listExporters() {
		return exporterRepository.findAll().stream().map(exporterMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public ExporterDto updateExporter(Integer id, ExporterDto exporterDto) {
		Exporter existingExporter = exporterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exportateur non trouvé"));

		Exporter updatedExporter = exporterMapper.partialUpdate(exporterDto, existingExporter);
		Exporter saved = exporterRepository.save(updatedExporter);
		return exporterMapper.toDto(saved);
	}

	@Override
	public void deleteExporter(Integer id) {
		if (!exporterRepository.existsById(id)) {
			throw new ResourceNotFoundException("Exportateur non trouvé");
		}
		exporterRepository.deleteById(id);
	}
}
