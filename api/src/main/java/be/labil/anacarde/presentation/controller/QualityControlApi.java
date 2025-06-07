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

/**
 * API REST pour la gestion des contrôles qualité.
 * <p>
 * Fournit les opérations CRUD et de téléversement associées aux entités QualityControl :
 * <ul>
 * <li>Obtenir un contrôle qualité par son ID.</li>
 * <li>Créer un contrôle qualité avec possibilité de téléverser plusieurs documents.</li>
 * <li>Mettre à jour un contrôle qualité existant.</li>
 * <li>Lister tous les contrôles qualité.</li>
 * <li>Supprimer un contrôle qualité.</li>
 * </ul>
 * Toutes les requêtes sont sécurisées via JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/quality-controls", produces = "application/json")
@Tag(name = "quality-controls", description = "Gestion des contrôles qualité")
public interface QualityControlApi {

	/**
	 * Récupère un contrôle qualité par son identifiant.
	 *
	 * @param id
	 *            Identifiant du contrôle qualité (entier positif)
	 * @return {@code 200 OK} avec le {@link QualityControlDto} correspondant, ou
	 *         {@code 404 Not Found} avec un {@link ApiErrorResponse}
	 */
	@Operation(summary = "Obtenir un contrôle qualité")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = QualityControlDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<QualityControlDto> getQualityControl(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée un nouveau contrôle qualité et téléverse les documents associés.
	 * <p>
	 * Attend une requête {@code multipart/form-data} contenant :
	 * <ul>
	 * <li>Un champ {@code qualityControl} JSON décrivant le contrôle qualité.</li>
	 * <li>Un champ {@code documents} optionnel, contenant un tableau de fichiers.</li>
	 * </ul>
	 *
	 * @param qualityControl
	 *            DTO de création, validé selon {@link ValidationGroups.Create}
	 * @param documents
	 *            liste de fichiers à associer, peut être vide ou null
	 * @return {@code 201 Created} avec le {@link QualityControlDto} créé, ou
	 *         {@code 400 Bad Request}/{@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
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

	/**
	 * Met à jour un contrôle qualité existant.
	 *
	 * @param id
	 *            Identifiant du contrôle qualité à mettre à jour
	 * @param qualityControlDto
	 *            DTO contenant les nouvelles valeurs, validé selon {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link QualityControlDto} mis à jour, ou
	 *         {@code 400 Bad Request}/{@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Mettre à jour un contrôle qualité")
	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = QualityControlDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<QualityControlDto> updateQualityControl(
			@ApiValidId @PathVariable("id") Integer id, @Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody QualityControlUpdateDto qualityControlDto);

	/**
	 * Liste tous les contrôles qualité.
	 *
	 * @return {@code 200 OK} avec la liste de {@link QualityControlDto}
	 */
	@Operation(summary = "Lister tous les contrôles qualité d’un produit")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = QualityControlDto.class))))})
	ResponseEntity<List<QualityControlDto>> listQualityControls();

	/**
	 * Supprime un contrôle qualité par son identifiant.
	 *
	 * @param id
	 *            Identifiant du contrôle qualité à supprimer
	 * @return {@code 204 No Content} si la suppression réussit, ou {@code 404 Not Found} avec
	 *         {@link ApiErrorResponse}
	 */
	@Operation(summary = "Supprimer un contrôle qualité")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteQualityControl(@ApiValidId @PathVariable("id") Integer id);
}
