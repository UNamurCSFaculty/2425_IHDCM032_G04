package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.RegionDto;
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
@RequestMapping(value = "/api/regions", produces = "application/json")
@Tag(name = "regions", description = "Opérations relatives aux régions")
public interface RegionApi {

	@Operation(summary = "Obtenir une région par ID")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<? extends RegionDto> getRegion(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer une nouvelle région")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<? extends RegionDto> createRegion(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody RegionDto regionDto);

	@Operation(summary = "Mettre à jour une région")
	@PutMapping("/{id}")
	@ApiResponsePut
	ResponseEntity<? extends RegionDto> updateRegion(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody RegionDto regionDto);

	@Operation(summary = "Lister toutes les régions")
	@GetMapping
	@ApiResponseGet
	ResponseEntity<List<? extends RegionDto>> listRegions();

	@Operation(summary = "Supprimer une région")
	@DeleteMapping("/{id}")
	@ApiResponseDelete
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteRegion(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Associer une région à un transporteur")
	@PutMapping("{regionId}/carriers/{carrierId}")
	@ApiResponsePost
	@ResponseStatus(HttpStatus.OK)
	ResponseEntity<Void> addRegionToCarrier(@ApiValidId @PathVariable("carrierId") Integer carrierId,
			@ApiValidId @PathVariable("regionId") Integer regionId);
}
