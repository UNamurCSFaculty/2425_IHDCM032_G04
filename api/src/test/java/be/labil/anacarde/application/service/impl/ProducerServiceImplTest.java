package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.ProducerServiceImpl;
import be.labil.anacarde.domain.dto.ProducerDto;
import be.labil.anacarde.domain.mapper.ProducerMapper;
import be.labil.anacarde.domain.model.Bidder;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.infrastructure.persistence.ProducerRepository;
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
public class ProducerServiceImplTest {

	@Mock
	private ProducerRepository producerRepository;

	@Mock
	private ProducerMapper producerMapper;

	@InjectMocks
	private ProducerServiceImpl producerService;

	private Producer producer;
	private ProducerDto producerDto;

	@BeforeEach
	void setUp() {
		producer = new Producer();
		producer.setId(1);
		Cooperative cooperative = new Cooperative();
		cooperative.setId(1);
		producer.setCooperative(cooperative);

		producerDto = new ProducerDto();
		producerDto.setId(1);
		producerDto.setCooperativeId(1);

		Mockito.lenient().when(producerMapper.toEntity(any(ProducerDto.class))).thenReturn(producer);
		Mockito.lenient().when(producerMapper.toDto(any(Producer.class))).thenReturn(producerDto);
	}

	@Test
	void testCreateProducer() {
		when(producerRepository.save(producer)).thenReturn(producer);
		ProducerDto result = producerService.createProducer(producerDto);
		assertThat(result).isEqualTo(producerDto);
		verify(producerRepository, times(1)).save(producer);
	}

	@Test
	void testGetProducerByIdSuccess() {
		when(producerRepository.findById(1)).thenReturn(Optional.of(producer));
		ProducerDto result = producerService.getProducerById(1);
		assertThat(result).isEqualTo(producerDto);
	}

	@Test
	void testGetProducerByIdNotFound() {
		when(producerRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> producerService.getProducerById(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Producteur non trouvé");
	}

	@Test
	void testListProducers() {
		when(producerRepository.findAll()).thenReturn(Collections.singletonList(producer));
		List<ProducerDto> result = producerService.listProducers();
		assertThat(result).hasSize(1).contains(producerDto);
	}

	@Test
	void testUpdateProducer() {
		ProducerDto updatedDto = new ProducerDto();
		updatedDto.setCooperativeId(2);
		updatedDto.setBidderId(3);
		Cooperative cooperative = new Cooperative();
		Bidder bidder = new Bidder();
		when(producerRepository.findById(1)).thenReturn(Optional.of(producer));
		when(producerMapper.partialUpdate(any(ProducerDto.class), any(Producer.class))).thenAnswer(invocation -> {
			ProducerDto dto = invocation.getArgument(0);
			Producer existing = invocation.getArgument(1);
			if (dto.getCooperativeId() != null) {
				cooperative.setId(dto.getCooperativeId());
				existing.setCooperative(cooperative);
			}
			if (dto.getBidderId() != null) {
				bidder.setId(dto.getBidderId());
				existing.setBidder(bidder);
			}
			return existing;
		});
		when(producerRepository.save(producer)).thenReturn(producer);
		when(producerMapper.toDto(producer)).thenReturn(updatedDto);

		ProducerDto result = producerService.updateProducer(1, updatedDto);
		assertThat(result.getCooperativeId()).isEqualTo(2);
		assertThat(result.getBidderId()).isEqualTo(3);
	}

	@Test
	void testDeleteProducerSuccess() {
		when(producerRepository.existsById(1)).thenReturn(true);
		producerService.deleteProducer(1);
		verify(producerRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeleteProducerNotFound() {
		when(producerRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> producerService.deleteProducer(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Producteur non trouvé");
	}
}
