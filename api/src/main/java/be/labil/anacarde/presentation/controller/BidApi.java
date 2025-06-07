package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.BidDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
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
 * API REST pour la gestion des offres (bids).
 * <p>
 * Définit les points d’accès pour créer, mettre à jour, accepter,
 * rejeter, récupérer et supprimer des offres d’enchères.
 * Toutes les opérations sont sécurisées via JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/bids", produces = "application/json")
@Tag(name = "bids", description = "Gestion des offres")
public interface BidApi {

	/**
	 * Récupère une offre par son identifiant.
	 *
	 * @param bidId Identifiant de l’offre (doit être positif et non null)
	 * @return {@code 200 OK} avec un {@link BidDto}, ou
	 *         {@code 404 Not Found} avec un {@link ApiErrorResponse}
	 */
	@Operation(summary = "Obtenir une offre")
	@GetMapping("/{bidId}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = BidDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<BidDto> getBid(@ApiValidId @PathVariable("bidId") Integer bidId);

	/**
	 * Crée une nouvelle offre.
	 *
	 * @param bidDto DTO contenant les données d’une nouvelle offre,
	 *               validé selon {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link BidDto} créé, ou
	 *         {@code 400 Bad Request} ou {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Créer une offre")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = BidDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<BidDto> createBid(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody BidUpdateDto bidDto);

	/**
	 * Met à jour une offre existante.
	 *
	 * @param bidId  Identifiant de l’offre à mettre à jour
	 * @param bidDto DTO contenant les données mises à jour,
	 *               validé selon {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link BidDto} mis à jour, ou
	 *         {@code 400 Bad Request} ou {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Mettre à jour une offre")
	@PutMapping(value = "/{bidId}")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = BidDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<BidDto> updateBid(@ApiValidId @PathVariable("bidId") Integer bidId, @Validated({
			Default.class, ValidationGroups.Update.class}) @RequestBody BidUpdateDto bidDto);

	/**
	 * Accepte (valide) une offre existante.
	 *
	 * @param bidId Identifiant de l’offre à accepter
	 * @return {@code 200 OK} avec le {@link BidDto} accepté, ou
	 *         {@code 404 Not Found} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Accepter une offre")
	@PutMapping(value = "/{bidId}/accept")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = BidDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<BidDto> acceptBid(@ApiValidId @PathVariable("bidId") Integer bidId);

	/**
	 * Rejette une offre existante.
	 *
	 * @param bidId Identifiant de l’offre à rejeter
	 * @return {@code 200 OK} avec le {@link BidDto} rejeté, ou
	 *         {@code 404 Not Found} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Rejeter une offre")
	@PutMapping(value = "/{bidId}/reject")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = BidDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<BidDto> rejectBid(@ApiValidId @PathVariable("bidId") Integer bidId);

	/**
	 * Liste toutes les offres, optionnellement filtrées par enchère.
	 *
	 * @param auctionId (optionnel) Identifiant de l’enchère dont on veut les offres
	 * @return {@code 200 OK} avec la liste de {@link BidDto}
	 */
	@Operation(summary = "Obtenir toutes les offres")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BidDto.class))))})
	ResponseEntity<List<BidDto>> listBids(
			@Parameter(description = "ID de l'enchère contenant les offres") @RequestParam(value = "auctionId", required = false) Integer auctionId);

	/**
	 * Supprime une offre.
	 *
	 * @param bidId Identifiant de l’offre à supprimer
	 * @return {@code 204 No Content} si la suppression réussit, ou
	 *         {@code 404 Not Found} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Supprimer une offre")
	@DeleteMapping("/{bidId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteBid(@ApiValidId @PathVariable("bidId") Integer bidId);
}
