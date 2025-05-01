package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.StoreService;
import be.labil.anacarde.domain.dto.StoreDetailDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class StoreApiController implements StoreApi {
	private final StoreService storeService;

	@Override
	public ResponseEntity<StoreDetailDto> getStore(Integer id) {
		StoreDetailDto store = storeService.getStoreById(id);
		return ResponseEntity.ok(store);
	}

	@Override
	public ResponseEntity<List<StoreDetailDto>> listStores() {
		List<StoreDetailDto> stores = storeService.listStores();
		return ResponseEntity.ok(stores);
	}

	@Override
	public ResponseEntity<StoreDetailDto> createStore(StoreDetailDto storeDetailDto) {
		StoreDetailDto created = storeService.createStore(storeDetailDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<StoreDetailDto> updateStore(Integer id, StoreDetailDto storeDetailDto) {
		StoreDetailDto updated = storeService.updateStore(id, storeDetailDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteStore(Integer id) {
		storeService.deleteStore(id);
		return ResponseEntity.noContent().build();
	}
}
