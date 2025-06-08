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

/**
 * API REST pour la gestion des coopératives.
 * <p>
 * Fournit les opérations suivantes :
 * <ul>
 * <li>Récupérer une coopérative par son ID.</li>
 * <li>Créer une nouvelle coopérative.</li>
 * <li>Mettre à jour une coopérative existante.</li>
 * <li>Lister toutes les coopératives.</li>
 * <li>Supprimer une coopérative.</li>
 * </ul>
 * Toutes les requêtes sont sécurisées via JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/cooperatives", produces = "application/json")
@Tag(name = "cooperatives", description = "Gestion des coopératives")
public interface CooperativeApi {

	/**
	 * Obtient une coopérative par son identifiant.
	 *
	 * @param id
	 *            Identifiant de la coopérative (doit être un entier positif)
	 * @return {@code 200 OK} avec le {@link CooperativeDto}, ou {@code 404 Not Found} avec un
	 *         {@link ApiErrorResponse} si introuvable
	 */
	@Operation(summary = "Obtenir une coopérative")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = CooperativeDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<CooperativeDto> getCooperative(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée une nouvelle coopérative.
	 *
	 * @param cooperativeDto
	 *            DTO contenant les données de création, validé selon
	 *            {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link CooperativeDto} créé, ou {@code 400 Bad Request} /
	 *         {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Créer une coopérative")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = CooperativeDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<CooperativeDto> createCooperative(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody CooperativeUpdateDto cooperativeDto);

	/**
	 * Met à jour une coopérative existante.
	 *
	 * @param id
	 *            Identifiant de la coopérative à mettre à jour
	 * @param cooperativeDto
	 *            DTO contenant les données de mise à jour, validé selon
	 *            {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link CooperativeDto} mis à jour, ou {@code 400 Bad Request}
	 *         / {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Mettre à jour une coopérative")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = CooperativeDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<CooperativeDto> updateCooperative(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody CooperativeUpdateDto cooperativeDto);

	/**
	 * Récupère la liste de toutes les coopératives.
	 *
	 * @return {@code 200 OK} avec la liste de {@link CooperativeDto}
	 */
	@Operation(summary = "Lister toutes les coopératives")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CooperativeDto.class))))})
	ResponseEntity<List<CooperativeDto>> listCooperatives();

	/**
	 * Supprime une coopérative par son identifiant.
	 *
	 * @param id
	 *            Identifiant de la coopérative à supprimer
	 */
	@Operation(summary = "Supprimer une coopérative")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteCooperative(@ApiValidId @PathVariable("id") Integer id);
}
