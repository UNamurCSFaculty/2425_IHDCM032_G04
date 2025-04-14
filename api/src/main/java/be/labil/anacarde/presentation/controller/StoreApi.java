package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.StoreDetailDto;
import be.labil.anacarde.domain.dto.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/stores", produces = "application/json")
@Tag(name = "stores", description = "Opérations relatives aux magasins")
public interface StoreApi {

	@Operation(summary = "Obtenir un magasin")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<? extends StoreDetailDto> getStore(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un magasin")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<? extends StoreDetailDto> createStore(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody StoreDetailDto storeDetailDto);

	@Operation(summary = "Mettre à jour un magasin")
	@ApiResponsePut
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<? extends StoreDetailDto> updateStore(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody StoreDetailDto storeDetailDto);

	@Operation(summary = "Obtenir tous les magasins")
	@ApiResponseGet
	@GetMapping
	ResponseEntity<List<? extends StoreDetailDto>> listStores();

	@Operation(summary = "Supprimer un magasin")
	@ApiResponseDelete
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteStore(@ApiValidId @PathVariable("id") Integer id);
}
