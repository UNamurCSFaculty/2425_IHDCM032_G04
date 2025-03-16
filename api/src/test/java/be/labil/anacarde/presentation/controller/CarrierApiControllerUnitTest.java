package be.labil.anacarde.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.service.CarrierService;
import be.labil.anacarde.domain.dto.CarrierDto;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CarrierApiControllerUnitTest {

	@Mock
	private CarrierService carrierService;

	@InjectMocks
	private CarrierApiController carrierApiController;

	private CarrierDto carrierDto;

	@BeforeEach
	void setUp() {
		carrierDto = new CarrierDto();
		carrierDto.setId(1);
		carrierDto.setGpsLocation("GEOGRAPHY(POINT, 4326)");
		carrierDto.setKmRange(new BigDecimal("100.0"));
		carrierDto.setKmPrice(new BigDecimal("5.0"));

	}

	@Test
	void testCreateCarrier() {
		when(carrierService.createCarrier(any(CarrierDto.class))).thenReturn(carrierDto);
		ResponseEntity<CarrierDto> response = carrierApiController.createCarrier(carrierDto);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(1, response.getBody().getId());
	}

	@Test
	void testGetCarrierById() {
		when(carrierService.getCarrierById(1)).thenReturn(carrierDto);
		ResponseEntity<CarrierDto> response = carrierApiController.getCarrier(1);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().getId());
	}

	@Test
	void testListCarriers() {
		when(carrierService.listCarriers()).thenReturn(Collections.singletonList(carrierDto));
		ResponseEntity<List<CarrierDto>> response = carrierApiController.listCarriers();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().get(0).getId());
	}

	@Test
	void testUpdateCarrier() {
		when(carrierService.updateCarrier(eq(1), any(CarrierDto.class))).thenReturn(carrierDto);
		ResponseEntity<CarrierDto> response = carrierApiController.updateCarrier(1, carrierDto);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().getId());
	}

	@Test
	void testDeleteCarrier() {
		doNothing().when(carrierService).deleteCarrier(1);
		ResponseEntity<Void> response = carrierApiController.deleteCarrier(1);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
}
