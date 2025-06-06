package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.RegionDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.ApiValidId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * API pour la gestion des régions. Permet de gérer les Gestion des régions, telles que la création,
 * la mise à jour, la suppression et la récupération des régions.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/regions", produces = "application/json")
@Tag(name = "regions", description = "Gestion des régions")
public interface RegionApi {

	@Operation(summary = "Obtenir une région par ID")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = RegionDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<RegionDto> getRegion(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Mettre à jour une région")
	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = RegionDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<RegionDto> updateRegion(@ApiValidId @PathVariable("id") Integer id, @Validated({
			Default.class, ValidationGroups.Update.class}) @RequestBody RegionDto regionDto);

	@Operation(summary = "Lister toutes les régions")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RegionDto.class))))})
	ResponseEntity<List<RegionDto>> listRegions();

	@Operation(summary = "Supprimer une région")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteRegion(@ApiValidId @PathVariable("id") Integer id);
}
