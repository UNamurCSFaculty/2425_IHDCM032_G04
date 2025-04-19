package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.CooperativeDto;
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
@RequestMapping(value = "/api/cooperatives", produces = "application/json")
@Tag(name = "cooperatives", description = "Opérations relatives aux coopératives")
public interface CooperativeApi {

	@Operation(summary = "Obtenir une coopérative")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<? extends CooperativeDto> getCooperative(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer une coopérative")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<? extends CooperativeDto> createCooperative(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody CooperativeDto cooperativeDto);

	@Operation(summary = "Mettre à jour une coopérative")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponsePut
	ResponseEntity<? extends CooperativeDto> updateCooperative(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody CooperativeDto cooperativeDto);

	@Operation(summary = "Lister toutes les coopératives")
	@GetMapping
	@ApiResponseGet
	ResponseEntity<List<? extends CooperativeDto>> listCooperatives();

	@Operation(summary = "Supprimer une coopérative")
	@DeleteMapping("/{id}")
	@ApiResponseDelete
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteCooperative(@ApiValidId @PathVariable("id") Integer id);
}
