package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.StoreDetailDto;
import be.labil.anacarde.domain.dto.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/stores", produces = "application/json")
public interface StoreApi {

	@Operation(summary = "Get a store", description = "", tags = {"stores"})
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<? extends StoreDetailDto> getStore(
			@ApiValidId
			@PathVariable("id")
			Integer id);

	@Operation(summary = "Create a store", description = "", tags = {"stores"})
	@PostMapping
	@ApiResponsePost
	ResponseEntity<? extends StoreDetailDto> createStore(
			@Validated({Default.class, ValidationGroups.Create.class})
			@RequestBody
			StoreDetailDto storeDetailDto);

	@Operation(summary = "Update a store", description = "", tags = {"stores"})
	@ApiResponsePut
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<? extends StoreDetailDto> updateStore(
			@ApiValidId
			@PathVariable("id")
			Integer id,
			@Validated({Default.class, ValidationGroups.Update.class})
			@RequestBody
			StoreDetailDto storeDetailDto);


	@Operation(summary = "List all stores", description = "", tags = {"stores"})
	@ApiResponseGet
	@GetMapping
	ResponseEntity<List<? extends StoreDetailDto>> listStores();

	@Operation(summary = "Delete a store", description = "", tags= {"stores"})
	@ApiResponseDelete
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteStore(
			@ApiValidId
			@PathVariable("id")
			Integer id);
}
