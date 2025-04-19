package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.FieldDetailDto;
import be.labil.anacarde.domain.mapper.FieldDetailMapper;
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
	private final FieldDetailMapper fieldMapper;

	@Override
	public FieldDetailDto createField(FieldDetailDto dto) {
		Field field = fieldMapper.toEntity(dto);
		Field saved = fieldRepository.save(field);
		return fieldMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public FieldDetailDto getFieldById(Integer id) {
		Field field = fieldRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Champ non trouvé"));
		return fieldMapper.toDto(field);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FieldDetailDto> listFields(Integer userId) {
		List<Field> fields = fieldRepository.findByProducerId(userId);
		return fields.stream().map(fieldMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public FieldDetailDto updateField(Integer id, FieldDetailDto fieldDetailDto) {
		Field existingField = fieldRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Champ non trouvé"));

		Field updatedField = fieldMapper.partialUpdate(fieldDetailDto, existingField);

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
