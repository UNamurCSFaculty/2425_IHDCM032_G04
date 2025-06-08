package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.FieldUpdateDto;
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

/**
 * API REST pour la gestion des Champs (Field).
 * <p>
 * Fournit les opérations CRUD suivantes pour les entités Field :
 * <ul>
 * <li>Obtenir un champ par son identifiant.</li>
 * <li>Créer un nouveau champ.</li>
 * <li>Mettre à jour un champ existant.</li>
 * <li>Lister les champs, avec filtre facultatif par producteur.</li>
 * <li>Supprimer un champ par son identifiant.</li>
 * </ul>
 * Toutes les requêtes sont sécurisées via JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/fields", produces = "application/json")
@Tag(name = "fields", description = "Gestion des champs")
public interface FieldApi {

	/**
	 * Récupère un champ par son identifiant.
	 *
	 * @param id
	 *            Identifiant du champ (doit être un entier positif)
	 * @return {@code 200 OK} avec le {@link FieldDto} correspondant, ou {@code 404 Not Found} avec
	 *         {@link ApiErrorResponse}
	 */
	@Operation(summary = "Obtenir un champ")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = FieldDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<FieldDto> getField(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée un nouveau champ.
	 *
	 * @param fieldDto
	 *            DTO contenant les données du champ à créer, validé selon
	 *            {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link FieldDto} créé, ou {@code 400 Bad Request} /
	 *         {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Créer un champ")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = FieldUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<FieldDto> createField(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody FieldUpdateDto fieldDto);

	/**
	 * Met à jour un champ existant.
	 *
	 * @param id
	 *            Identifiant du champ à mettre à jour
	 * @param fieldDto
	 *            DTO contenant les nouvelles valeurs, validé selon {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link FieldDto} mis à jour, ou {@code 400 Bad Request} /
	 *         {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Mettre à jour un champ")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = FieldUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<FieldDto> updateField(@ApiValidId @PathVariable("id") Integer id, @Validated({
			Default.class, ValidationGroups.Update.class}) @RequestBody FieldUpdateDto fieldDto);

	/**
	 * Liste tous les champs, avec un filtre facultatif par producteur.
	 *
	 * @param producerId
	 *            (optionnel) Identifiant du producteur pour filtrer les champs
	 * @return {@code 200 OK} avec la liste de {@link FieldDto}
	 */
	@Operation(summary = "Obtenir tous les champs")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FieldDto.class))))})
	ResponseEntity<List<FieldDto>> listFields(
			@Parameter(description = "ID du producteur possédant le champ") @RequestParam(value = "producerId", required = false) Integer producerId);

	/**
	 * Supprime un champ par son identifiant.
	 *
	 * @param id
	 *            Identifiant du champ à supprimer
	 * @return {@code 204 No Content} si la suppression réussit, ou {@code 404 Not Found} avec
	 *         {@link ApiErrorResponse}
	 */
	@Operation(summary = "Supprimer un champ")
	@DeleteMapping("/{id}")
	@ApiResponseDelete
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteField(@ApiValidId @PathVariable("id") Integer id);
}
