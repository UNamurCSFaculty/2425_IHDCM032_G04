package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.TransformerServiceImpl;
import be.labil.anacarde.domain.dto.TransformerDto;
import be.labil.anacarde.domain.mapper.TransformerMapper;
import be.labil.anacarde.domain.model.Transformer;
import be.labil.anacarde.infrastructure.persistence.TransformerRepository;
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
public class TransformerServiceImplTest {

	@Mock
	private TransformerRepository transformerRepository;

	@Mock
	private TransformerMapper transformerMapper;

	@InjectMocks
	private TransformerServiceImpl transformerService;

	private Transformer transformer;
	private TransformerDto transformerDto;

	@BeforeEach
	void setUp() {
		transformer = new Transformer();
		transformer.setId(1);
		transformer.setProductType("amande");

		transformerDto = new TransformerDto();
		transformerDto.setId(1);
		transformerDto.setProductType("amande");

		Mockito.lenient().when(transformerMapper.toEntity(any(TransformerDto.class))).thenReturn(transformer);
		Mockito.lenient().when(transformerMapper.toDto(any(Transformer.class))).thenReturn(transformerDto);
	}

	@Test
	void testCreateTransformer() {
		when(transformerRepository.save(transformer)).thenReturn(transformer);
		TransformerDto result = transformerService.createTransformer(transformerDto);
		assertThat(result).isEqualTo(transformerDto);
		verify(transformerRepository, times(1)).save(transformer);
	}

	@Test
	void testGetTransformerByIdSuccess() {
		when(transformerRepository.findById(1)).thenReturn(Optional.of(transformer));
		TransformerDto result = transformerService.getTransformerById(1);
		assertThat(result).isEqualTo(transformerDto);
	}

	@Test
	void testGetTransformerByIdNotFound() {
		when(transformerRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> transformerService.getTransformerById(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Transformateur non trouvé");
	}

	@Test
	void testListTransformers() {
		when(transformerRepository.findAll()).thenReturn(Collections.singletonList(transformer));
		List<TransformerDto> result = transformerService.listTransformers();
		assertThat(result).hasSize(1).contains(transformerDto);
	}

	@Test
	void testUpdateTransformerSuccess() {
		TransformerDto updatedDto = new TransformerDto();
		updatedDto.setProductType("noix");

		when(transformerRepository.findById(1)).thenReturn(Optional.of(transformer));
		when(transformerMapper.partialUpdate(any(TransformerDto.class), any(Transformer.class)))
				.thenAnswer(invocation -> {
					TransformerDto dto = invocation.getArgument(0);
					Transformer existing = invocation.getArgument(1);
					if (dto.getProductType() != null) {
						existing.setProductType(dto.getProductType());
					}
					return existing;
				});
		when(transformerRepository.save(transformer)).thenReturn(transformer);
		when(transformerMapper.toDto(transformer)).thenReturn(updatedDto);

		TransformerDto result = transformerService.updateTransformer(1, updatedDto);
		assertThat(result.getProductType()).isEqualTo("noix");
	}

	@Test
	void testDeleteTransformerSuccess() {
		when(transformerRepository.existsById(1)).thenReturn(true);
		transformerService.deleteTransformer(1);
		verify(transformerRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeleteTransformerNotFound() {
		when(transformerRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> transformerService.deleteTransformer(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Transformateur non trouvé");
	}
}