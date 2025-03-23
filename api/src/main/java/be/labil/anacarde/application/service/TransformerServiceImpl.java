package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.TransformerDto;
import be.labil.anacarde.domain.mapper.TransformerMapper;
import be.labil.anacarde.domain.model.Transformer;
import be.labil.anacarde.infrastructure.persistence.TransformerRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
/**
 * Ce service gère les opérations relatives aux transformateurs.
 */
public class TransformerServiceImpl implements TransformerService {

	private final TransformerRepository transformerRepository;
	private final TransformerMapper transformerMapper;

	@Override
	public TransformerDto createTransformer(TransformerDto transformerDto) {
		Transformer transformer = transformerMapper.toEntity(transformerDto);
		Transformer saved = transformerRepository.save(transformer);
		return transformerMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public TransformerDto getTransformerById(Integer id) {
		Transformer transformer = transformerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transformateur non trouvé"));
		return transformerMapper.toDto(transformer);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransformerDto> listTransformers() {
		return transformerRepository.findAll().stream().map(transformerMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public TransformerDto updateTransformer(Integer id, TransformerDto transformerDto) {
		Transformer existingTransformer = transformerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transformateur non trouvé"));

		Transformer updatedTransformer = transformerMapper.partialUpdate(transformerDto, existingTransformer);
		Transformer saved = transformerRepository.save(updatedTransformer);
		return transformerMapper.toDto(saved);
	}

	@Override
	public void deleteTransformer(Integer id) {
		if (!transformerRepository.existsById(id)) {
			throw new ResourceNotFoundException("Transformateur non trouvé");
		}
		transformerRepository.deleteById(id);
	}
}
