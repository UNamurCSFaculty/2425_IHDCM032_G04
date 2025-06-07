package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.StoreUpdateDto;
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
 * API REST pour la gestion des magasins (stores).
 * <p>
 * Fournit les opérations CRUD suivantes :
 * <ul>
 * <li>Récupérer un magasin par son identifiant.</li>
 * <li>Créer un nouveau magasin.</li>
 * <li>Mettre à jour un magasin existant.</li>
 * <li>Lister tous les magasins.</li>
 * <li>Supprimer un magasin par son identifiant.</li>
 * </ul>
 * Toutes les méthodes nécessitent une authentification JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/stores", produces = "application/json")
@Tag(name = "stores", description = "Gestion des magasins")
public interface StoreApi {

	/**
	 * Récupère un magasin par son identifiant.
	 *
	 * @param id
	 *            Identifiant du magasin (entier positif, non null)
	 * @return {@code 200 OK} contenant le {@link StoreDetailDto}, ou {@code 404 Not Found} avec un
	 *         {@link ApiErrorResponse}
	 */
	@Operation(summary = "Obtenir un magasin")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = StoreDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<StoreDetailDto> getStore(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée un nouveau magasin.
	 *
	 * @param storeUpdateDto
	 *            DTO contenant les données du magasin à créer, validé selon
	 *            {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link StoreDetailDto} créé, ou
	 *         {@code 400 Bad Request}/{@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Créer un magasin")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = StoreUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<StoreDetailDto> createStore(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody StoreUpdateDto storeUpdateDto);

	/**
	 * Met à jour un magasin existant.
	 *
	 * @param id
	 *            Identifiant du magasin à mettre à jour
	 * @param storeUpdateDto
	 *            DTO contenant les nouvelles valeurs, validé selon {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link StoreDetailDto} mis à jour, ou
	 *         {@code 400 Bad Request}/{@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Mettre à jour un magasin")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = StoreUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<StoreDetailDto> updateStore(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody StoreUpdateDto storeUpdateDto);

	/**
	 * Récupère la liste de tous les magasins.
	 *
	 * @return {@code 200 OK} avec la liste de {@link StoreDetailDto}
	 */
	@Operation(summary = "Obtenir tous les magasins")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StoreDetailDto.class))))})
	ResponseEntity<List<StoreDetailDto>> listStores();

	/**
	 * Supprime un magasin par son identifiant.
	 *
	 * @param id
	 *            Identifiant du magasin à supprimer
	 * @return {@code 204 No Content} si la suppression réussit, ou {@code 404 Not Found} avec un
	 *         {@link ApiErrorResponse}
	 */
	@Operation(summary = "Supprimer un magasin")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteStore(@ApiValidId @PathVariable("id") Integer id);
}
