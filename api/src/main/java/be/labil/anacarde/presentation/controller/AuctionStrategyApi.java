package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.AuctionStrategyDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * API REST pour la gestion des stratégies d’enchères.
 * <p>
 * Définit les opérations CRUD pour l’entité {@link AuctionStrategyDto} :
 * <ul>
 *   <li>Récupérer une stratégie par son identifiant.</li>
 *   <li>Créer une nouvelle stratégie.</li>
 *   <li>Mettre à jour une stratégie existante.</li>
 *   <li>Lister toutes les stratégies.</li>
 *   <li>Supprimer une stratégie.</li>
 * </ul>
 * <p>
 * Toutes les méthodes sont sécurisées via JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/auctions/strategies", produces = "application/json")
@Tag(name = "auctions")
public interface AuctionStrategyApi {

	/**
	 * Obtient une stratégie d’enchère par son identifiant.
	 *
	 * @param id Identifiant de la stratégie (doit être positif et non null)
	 * @return {@code 200 OK} avec le {@link AuctionStrategyDto}, ou
	 *         {@code 404 Not Found} avec {@link ApiErrorResponse} si introuvable
	 */
	@Operation(summary = "Obtenir une stratégie d'enchère")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = AuctionStrategyDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<AuctionStrategyDto> getAuctionStrategy(
			@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée une nouvelle stratégie d’enchère.
	 *
	 * @param auctionStrategyDto DTO validé selon {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link AuctionStrategyDto}, ou
	 *         {@code 400 Bad Request} ou {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Créer une stratégie d'enchère")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = AuctionStrategyDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<AuctionStrategyDto> createAuctionStrategy(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody AuctionStrategyDto auctionStrategyDto);

	/**
	 * Met à jour une stratégie d’enchère existante.
	 *
	 * @param id                  Identifiant de la stratégie à mettre à jour
	 * @param auctionStrategyDto  DTO validé selon {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link AuctionStrategyDto}, ou
	 *         {@code 400 Bad Request} ou {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Mettre à jour une stratégie d'enchère")
	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = AuctionStrategyDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<AuctionStrategyDto> updateAuctionStrategy(
			@ApiValidId @PathVariable("id") Integer id, @Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody AuctionStrategyDto auctionStrategyDto);

	/**
	 * Récupère la liste de toutes les stratégies d’enchères.
	 *
	 * @return {@code 200 OK} avec la liste de {@link AuctionStrategyDto}
	 */
	@Operation(summary = "Obtenir toutes les stratégies d'enchère")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuctionStrategyDto.class))))})
	ResponseEntity<List<AuctionStrategyDto>> listAuctionStrategies();

	/**
	 * Supprime une stratégie d’enchère par son identifiant.
	 *
	 * @param id Identifiant de la stratégie à supprimer
	 * @return {@code 204 No Content} si supprimée, ou
	 *         {@code 404 Not Found} avec {@link ApiErrorResponse} si introuvable
	 */
	@Operation(summary = "Supprimer une stratégie d'enchère")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteAuctionStrategy(@ApiValidId @PathVariable("id") Integer id);
}
