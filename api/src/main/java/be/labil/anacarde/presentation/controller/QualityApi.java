package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.QualityDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.QualityUpdateDto;
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
 * API REST pour la gestion des entités Quality.
 * <p>
 * Fournit les opérations CRUD suivantes :
 * <ul>
 * <li>Obtenir une qualité par son identifiant.</li>
 * <li>Créer une nouvelle qualité.</li>
 * <li>Mettre à jour une qualité existante.</li>
 * <li>Lister toutes les qualités.</li>
 * <li>Supprimer une qualité par son identifiant.</li>
 * </ul>
 * Toutes les méthodes requièrent une authentification via JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/qualities", produces = "application/json")
@Tag(name = "qualities", description = "Gestion des qualités")
public interface QualityApi {

	/**
	 * Récupère une qualité par son identifiant.
	 *
	 * @param id
	 *            Identifiant de la qualité (doit être un entier positif)
	 * @return {@code 200 OK} avec le {@link QualityDto} correspondant, ou {@code 404 Not Found}
	 *         avec un {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Obtenir une qualité")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = QualityDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<QualityDto> getQuality(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée une nouvelle qualité.
	 *
	 * @param qualityDto
	 *            DTO contenant les données de la qualité à créer, validé selon
	 *            {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link QualityDto} créé, ou
	 *         {@code 400 Bad Request}/{@code 409 Conflict} avec {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Créer une qualité")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = QualityDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<QualityDto> createQuality(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody QualityUpdateDto qualityDto);

	/**
	 * Met à jour une qualité existante.
	 *
	 * @param id
	 *            Identifiant de la qualité à mettre à jour
	 * @param qualityDto
	 *            DTO contenant les nouvelles valeurs, validé selon {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link QualityDto} mis à jour, ou
	 *         {@code 400 Bad Request}/{@code 409 Conflict} avec {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Mettre à jour une qualité")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = QualityDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<QualityDto> updateQuality(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody QualityUpdateDto qualityDto);

	/**
	 * Récupère la liste de toutes les qualités.
	 *
	 * @return {@code 200 OK} avec la liste de {@link QualityDto}.
	 */
	@Operation(summary = "Obtenir toutes les qualités")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = QualityDto.class))))})
	ResponseEntity<List<QualityDto>> listQualities();

	/**
	 * Supprime une qualité par son identifiant.
	 *
	 * @param id
	 *            Identifiant de la qualité à supprimer
	 * @return {@code 204 No Content} si la suppression réussit, ou {@code 404 Not Found} avec
	 *         {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Supprimer une qualité")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteQuality(@ApiValidId @PathVariable("id") Integer id);
}
