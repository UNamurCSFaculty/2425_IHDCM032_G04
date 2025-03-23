package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.FieldDto;
import be.labil.anacarde.domain.mapper.FieldMapper;
import be.labil.anacarde.domain.model.Field;
import be.labil.anacarde.infrastructure.persistence.FieldRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class FieldServiceImpl implements FieldService {

	private final FieldRepository fieldRepository;
	private final FieldMapper fieldMapper;

	@Override
	public FieldDto createField(FieldDto fieldDto) {
		Field field = fieldMapper.toEntity(fieldDto);
		Field saved = fieldRepository.save(field);
		return fieldMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public FieldDto getFieldById(Integer id) {
		Field field = fieldRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Champ non trouvé"));
		return fieldMapper.toDto(field);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FieldDto> listFields() {
		return fieldRepository.findAll().stream().map(fieldMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public FieldDto updateField(Integer id, FieldDto fieldDto) {
		Field existingField = fieldRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Champ non trouvé"));

		Field updatedField = fieldMapper.partialUpdate(fieldDto, existingField);
		Field saved = fieldRepository.save(updatedField);
		return fieldMapper.toDto(saved);
	}

	@Override
	public void deleteField(Integer id) {
		if (!fieldRepository.existsById(id)) {
			throw new ResourceNotFoundException("Champ non trouvé");
		}
		fieldRepository.deleteById(id);
	}
}
