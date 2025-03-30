package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.CarrierServiceImpl;
import be.labil.anacarde.domain.dto.CarrierDto;
import be.labil.anacarde.domain.mapper.CarrierMapper;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.infrastructure.persistence.CarrierRepository;
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
public class CarrierServiceImplTest {

	@Mock
	private CarrierRepository carrierRepository;

	@Mock
	private CarrierMapper carrierMapper;

	@InjectMocks
	private CarrierServiceImpl carrierService;

	private Carrier carrier;
	private CarrierDto carrierDto;

	@BeforeEach
	void setUp() {
		carrier = new Carrier();
		carrier.setId(1);
		carrier.setGpsLocation("GEOGRAPHY(POINT, 4326)");
		carrier.setKmRange(new BigDecimal("100.0"));
		carrier.setKmPrice(new BigDecimal("5.0"));

		carrierDto = new CarrierDto();
		carrierDto.setId(1);
		carrierDto.setGpsLocation("GEOGRAPHY(POINT, 4326)");
		carrierDto.setKmRange(new BigDecimal("100.0"));
		carrierDto.setKmPrice(new BigDecimal("5.0"));

		Mockito.lenient().when(carrierMapper.toEntity(any(CarrierDto.class))).thenReturn(carrier);
		Mockito.lenient().when(carrierMapper.toDto(any(Carrier.class))).thenReturn(carrierDto);
	}

	@Test
	void testCreateCarrier() {
		when(carrierRepository.save(carrier)).thenReturn(carrier);
		CarrierDto result = carrierService.createCarrier(carrierDto);
		assertThat(result).isEqualTo(carrierDto);
		verify(carrierRepository, times(1)).save(carrier);
	}

	@Test
	void testGetCarrierByIdSuccess() {
		when(carrierRepository.findById(1)).thenReturn(Optional.of(carrier));
		CarrierDto result = carrierService.getCarrierById(1);
		assertThat(result).isEqualTo(carrierDto);
	}

	@Test
	void testGetCarrierByIdNotFound() {
		when(carrierRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> carrierService.getCarrierById(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Transporteur non trouvé");
	}

	@Test
	void testListCarriers() {
		when(carrierRepository.findAll()).thenReturn(Collections.singletonList(carrier));
		List<CarrierDto> result = carrierService.listCarriers();
		assertThat(result).hasSize(1).contains(carrierDto);
		verify(carrierRepository, times(1)).findAll();
	}

	@Test
	void testUpdateCarrier() {
		CarrierDto updatedDto = new CarrierDto();
		updatedDto.setKmRange(new BigDecimal("150.0"));
		updatedDto.setKmPrice(new BigDecimal("7.0"));

		when(carrierRepository.findById(1)).thenReturn(Optional.of(carrier));
		when(carrierMapper.partialUpdate(any(CarrierDto.class), any(Carrier.class))).thenAnswer(invocation -> {
			CarrierDto dto = invocation.getArgument(0);
			Carrier existing = invocation.getArgument(1);
			if (dto.getKmRange() != null) {
				existing.setKmRange(dto.getKmRange());
			}
			if (dto.getKmPrice() != null) {
				existing.setKmPrice(dto.getKmPrice());
			}
			return existing;
		});
		when(carrierRepository.save(carrier)).thenReturn(carrier);
		when(carrierMapper.toDto(carrier)).thenReturn(updatedDto);

		CarrierDto result = carrierService.updateCarrier(1, updatedDto);
		assertThat(result.getKmRange()).isEqualTo(new BigDecimal("150.0"));
		assertThat(result.getKmPrice()).isEqualTo(new BigDecimal("7.0"));

		verify(carrierRepository, times(1)).save(carrier);
	}

	@Test
	void testDeleteCarrierSuccess() {
		when(carrierRepository.existsById(1)).thenReturn(true);
		carrierService.deleteCarrier(1);
		verify(carrierRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeleteCarrierNotFound() {
		when(carrierRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> carrierService.deleteCarrier(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Transporteur non trouvé");
	}
}
