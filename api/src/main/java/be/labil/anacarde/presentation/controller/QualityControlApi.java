package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.QualityControlDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
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
@RequestMapping(value = "/api/products/{productId}/quality-controls", produces = "application/json")
@Tag(name = "quality-controls", description = "Opérations relatives aux contrôles qualité")
@Tag(name = "users", description = "Opérations relatives aux utilisateurs")
public interface QualityControlApi {

	@Operation(summary = "Obtenir un contrôle qualité")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = QualityControlDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<? extends QualityControlDto> getQualityControl(
			@ApiValidId @PathVariable("productId") Integer productId,
			@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un contrôle qualité")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = QualityControlDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<? extends QualityControlDto> createQualityControl(
			@ApiValidId @PathVariable("productId") Integer productId, @Validated({Default.class,
					ValidationGroups.Create.class}) @RequestBody QualityControlUpdateDto qualityControlDto);

	@Operation(summary = "Mettre à jour un contrôle qualité")
	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = QualityControlDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<? extends QualityControlDto> updateQualityControl(
			@ApiValidId @PathVariable("productId") Integer productId,
			@ApiValidId @PathVariable("id") Integer id, @Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody QualityControlUpdateDto qualityControlDto);

	@Operation(summary = "Lister tous les contrôles qualité d’un produit")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = QualityControlDto.class))))})
	ResponseEntity<List<? extends QualityControlDto>> listQualityControls(
			@PathVariable("productId") Integer productId);

	@Operation(summary = "Supprimer un contrôle qualité")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteQualityControl(
			@ApiValidId @PathVariable("productId") Integer productId,
			@ApiValidId @PathVariable("id") Integer id);
}
