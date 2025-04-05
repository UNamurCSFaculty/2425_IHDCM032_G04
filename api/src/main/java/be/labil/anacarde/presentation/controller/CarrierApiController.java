package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.CarrierService;
import be.labil.anacarde.domain.dto.CarrierDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class CarrierApiController implements CarrierApi {

	private final CarrierService carrierService;

	@Override
	public ResponseEntity<CarrierDto> getCarrier(Integer id) {
		CarrierDto carrier = carrierService.getCarrierById(id);
		return ResponseEntity.ok(carrier);
	}

	@Override
	public ResponseEntity<CarrierDto> createCarrier(CarrierDto carrierDto) {
		CarrierDto created = carrierService.createCarrier(carrierDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getUserId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<List<CarrierDto>> listCarriers() {
		List<CarrierDto> carriers = carrierService.listCarriers();
		return ResponseEntity.ok(carriers);
	}

	@Override
	public ResponseEntity<CarrierDto> updateCarrier(Integer id, CarrierDto carrierDto) {
		CarrierDto updated = carrierService.updateCarrier(id, carrierDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteCarrier(Integer id) {
		carrierService.deleteCarrier(id);
		return ResponseEntity.noContent().build();
	}

}
