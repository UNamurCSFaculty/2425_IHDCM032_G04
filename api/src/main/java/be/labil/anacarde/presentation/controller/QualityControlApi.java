package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.QualityControlDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import be.labil.anacarde.presentation.controller.annotations.ApiValidId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/quality-controls", produces = "application/json")
@Tag(name = "quality-controls", description = "Opérations relatives aux contrôles qualité")
public interface QualityControlApi {

	@Operation(summary = "Obtenir un contrôle qualité")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = QualityControlDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<QualityControlDto> getQualityControl(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un contrôle qualité et téléverser des documents")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "object", properties = {
			@StringToClassMapItem(key = "qualityControl", value = QualityControlUpdateDto.class),
			@StringToClassMapItem(key = "documents", value = MultipartFile[].class)}), encoding = {
					@Encoding(name = "qualityControl", contentType = MediaType.APPLICATION_JSON_VALUE),
					@Encoding(name = "documents", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)}))
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Contrôle qualité créé avec succès", content = @Content(schema = @Schema(implementation = QualityControlDto.class))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Conflit avec un utilisateur existant", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<QualityControlDto> createQualityControl(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestPart("qualityControl") @Valid QualityControlUpdateDto qualityControl,
			@RequestPart(value = "documents", required = false) List<MultipartFile> documents);

	@Operation(summary = "Mettre à jour un contrôle qualité")
	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = QualityControlDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<QualityControlDto> updateQualityControl(
			@ApiValidId @PathVariable("id") Integer id, @Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody QualityControlUpdateDto qualityControlDto);

	@Operation(summary = "Lister tous les contrôles qualité d’un produit")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = QualityControlDto.class))))})
	ResponseEntity<List<QualityControlDto>> listQualityControls();

	@Operation(summary = "Supprimer un contrôle qualité")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteQualityControl(@ApiValidId @PathVariable("id") Integer id);
}
