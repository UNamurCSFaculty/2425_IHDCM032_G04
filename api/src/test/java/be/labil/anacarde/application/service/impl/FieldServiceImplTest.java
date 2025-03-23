package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.FieldServiceImpl;
import be.labil.anacarde.domain.dto.FieldDto;
import be.labil.anacarde.domain.mapper.FieldMapper;
import be.labil.anacarde.domain.model.Field;
import be.labil.anacarde.infrastructure.persistence.FieldRepository;
import java.math.BigDecimal;
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
public class FieldServiceImplTest {

	@Mock
	private FieldRepository fieldRepository;

	@Mock
	private FieldMapper fieldMapper;

	@InjectMocks
	private FieldServiceImpl fieldService;

	private Field field;
	private FieldDto fieldDto;

	@BeforeEach
	void setUp() {
		field = new Field();
		field.setId(1);
		field.setLocation("GEOGRAPHY(POINT, 4326)");
		field.setSurfaceAreaM2(new BigDecimal("500.0"));
		field.setDetails("Sol argileux, riche en minéraux");

		fieldDto = new FieldDto();
		fieldDto.setId(1);
		fieldDto.setLocation("GEOGRAPHY(POINT, 4326)");
		fieldDto.setSurfaceAreaM2(new BigDecimal("500.0"));
		fieldDto.setDetails("Sol argileux, riche en minéraux");

		Mockito.lenient().when(fieldMapper.toEntity(any(FieldDto.class))).thenReturn(field);
		Mockito.lenient().when(fieldMapper.toDto(any(Field.class))).thenReturn(fieldDto);
	}

	@Test
	void testCreateField() {
		when(fieldRepository.save(field)).thenReturn(field);
		FieldDto result = fieldService.createField(fieldDto);
		assertThat(result).isEqualTo(fieldDto);
		verify(fieldRepository, times(1)).save(field);
	}

	@Test
	void testGetFieldByIdSuccess() {
		when(fieldRepository.findById(1)).thenReturn(Optional.of(field));
		FieldDto result = fieldService.getFieldById(1);
		assertThat(result).isEqualTo(fieldDto);
	}

	@Test
	void testGetFieldByIdNotFound() {
		when(fieldRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> fieldService.getFieldById(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Champ non trouvé");
	}

	@Test
	void testListFields() {
		when(fieldRepository.findAll()).thenReturn(Collections.singletonList(field));
		List<FieldDto> result = fieldService.listFields();
		assertThat(result).hasSize(1).contains(fieldDto);
	}

	@Test
	void testUpdateField() {
		FieldDto updatedDto = new FieldDto();
		updatedDto.setSurfaceAreaM2(new BigDecimal("750.0"));
		updatedDto.setDetails("Sol sablonneux, bien drainé");

		when(fieldRepository.findById(1)).thenReturn(Optional.of(field));
		when(fieldMapper.partialUpdate(any(FieldDto.class), any(Field.class))).thenAnswer(invocation -> {
			FieldDto dto = invocation.getArgument(0);
			Field existing = invocation.getArgument(1);
			if (dto.getSurfaceAreaM2() != null) {
				existing.setSurfaceAreaM2(dto.getSurfaceAreaM2());
			}
			if (dto.getDetails() != null) {
				existing.setDetails(dto.getDetails());
			}
			return existing;
		});
		when(fieldRepository.save(field)).thenReturn(field);
		when(fieldMapper.toDto(field)).thenReturn(updatedDto);

		FieldDto result = fieldService.updateField(1, updatedDto);
		assertThat(result.getSurfaceAreaM2()).isEqualTo(new BigDecimal("750.0"));
		assertThat(result.getDetails()).isEqualTo("Sol sablonneux, bien drainé");
	}

	@Test
	void testDeletefieldSuccess() {
		when(fieldRepository.existsById(1)).thenReturn(true);
		fieldService.deleteField(1);
		verify(fieldRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeletefieldNotFound() {
		when(fieldRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> fieldService.deleteField(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Champ non trouvé");
	}
}
