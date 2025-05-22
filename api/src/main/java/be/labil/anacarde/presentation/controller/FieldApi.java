package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.ApiResponseDelete;
import be.labil.anacarde.presentation.controller.annotations.ApiValidId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(value = "/api/fields", produces = "application/json")
@Tag(name = "fields", description = "Opérations relatives aux champs")
public interface FieldApi {

	@Operation(summary = "Obtenir un champ")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = FieldDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<FieldDto> getField(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un champ")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = FieldDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<FieldDto> createField(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody FieldDto fieldDto);

	@Operation(summary = "Mettre à jour un champ")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = FieldDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<FieldDto> updateField(@ApiValidId @PathVariable("id") Integer id, @Validated({
			Default.class, ValidationGroups.Update.class}) @RequestBody FieldDto fieldDto);

	@Operation(summary = "Obtenir tous les champs")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FieldDto.class))))})
	ResponseEntity<List<FieldDto>> listFields(
			@Parameter(description = "ID du producteur possédant le champ") @RequestParam(value = "producerId", required = false) Integer producerId);

	@Operation(summary = "Supprimer un champ")
	@DeleteMapping("/{id}")
	@ApiResponseDelete
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteField(@ApiValidId @PathVariable("id") Integer id);
}
