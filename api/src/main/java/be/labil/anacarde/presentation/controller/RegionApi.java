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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * API REST pour la gestion des régions.
 * <p>
 * Fournit les opérations CRUD suivantes pour les entités Region :
 * <ul>
 * <li>Récupérer une région par son identifiant.</li>
 * <li>Mettre à jour une région existante.</li>
 * <li>Lister toutes les régions.</li>
 * <li>Supprimer une région par son identifiant.</li>
 * </ul>
 * Toutes les méthodes nécessitent une authentification JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/regions", produces = "application/json")
@Tag(name = "regions", description = "Gestion des régions")
public interface RegionApi {

	/**
	 * Récupère une région par son identifiant.
	 *
	 * @param id
	 *            Identifiant de la région (doit être un entier positif)
	 * @return {@code 200 OK} avec le {@link RegionDto} correspondant, ou {@code 404 Not Found} avec
	 *         un {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Obtenir une région par ID")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = RegionDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<RegionDto> getRegion(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Met à jour une région existante.
	 *
	 * @param id
	 *            Identifiant de la région à mettre à jour
	 * @param regionDto
	 *            DTO contenant les nouvelles valeurs de la région, validé selon
	 *            {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link RegionDto} mis à jour, ou
	 *         {@code 400 Bad Request}/{@code 409 Conflict} avec {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Mettre à jour une région")
	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = RegionDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<RegionDto> updateRegion(@ApiValidId @PathVariable("id") Integer id, @Validated({
			Default.class, ValidationGroups.Update.class}) @RequestBody RegionDto regionDto);

	/**
	 * Récupère la liste de toutes les régions.
	 *
	 * @return {@code 200 OK} avec la liste de {@link RegionDto}.
	 */
	@Operation(summary = "Lister toutes les régions")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RegionDto.class))))})
	ResponseEntity<List<RegionDto>> listRegions();
}
