package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.CooperativeDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.CooperativeUpdateDto;
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

@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/cooperatives", produces = "application/json")
@Tag(name = "cooperatives", description = "Opérations relatives aux coopératives")
public interface CooperativeApi {

	@Operation(summary = "Obtenir une coopérative")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = CooperativeDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<CooperativeDto> getCooperative(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer une coopérative")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = CooperativeDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<CooperativeDto> createCooperative(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody CooperativeUpdateDto cooperativeDto);

	@Operation(summary = "Mettre à jour une coopérative")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = CooperativeDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<CooperativeDto> updateCooperative(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody CooperativeUpdateDto cooperativeDto);

	@Operation(summary = "Lister toutes les coopératives")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CooperativeDto.class))))})
	ResponseEntity<List<CooperativeDto>> listCooperatives();

	@Operation(summary = "Supprimer une coopérative")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteCooperative(@ApiValidId @PathVariable("id") Integer id);
}
