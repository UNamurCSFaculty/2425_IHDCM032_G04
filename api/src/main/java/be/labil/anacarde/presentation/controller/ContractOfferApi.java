package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.ContractOfferDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.ContractOfferUpdateDto;
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
 * API REST pour la gestion des offres de contrat.
 * <p>
 * Fournit les opérations CRUD et métiers suivantes :
 * <ul>
 * <li>Récupérer une offre par son ID.</li>
 * <li>Créer une nouvelle offre de contrat.</li>
 * <li>Mettre à jour une offre existante.</li>
 * <li>Accepter ou rejeter une offre.</li>
 * <li>Lister les offres avec des filtres facultatifs.</li>
 * <li>Supprimer une offre.</li>
 * </ul>
 * Toutes les méthodes sont sécurisées via JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/contracts", produces = "application/json")
@Tag(name = "contracts", description = "Gestion des contrats")
public interface ContractOfferApi {

	/**
	 * Récupère une offre de contrat par son identifiant.
	 *
	 * @param id
	 *            Identifiant de l’offre (positif, non null)
	 * @return {@code 200 OK} avec un {@link ContractOfferDto}, ou {@code 404 Not Found} avec un
	 *         {@link ApiErrorResponse}
	 */
	@Operation(summary = "Obtenir un contrat")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<ContractOfferDto> getContractOffer(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Crée une nouvelle offre de contrat.
	 *
	 * @param offerDto
	 *            DTO de création, validé selon {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec le {@link ContractOfferDto} créé, ou {@code 400 Bad Request}
	 *         / {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Créer un contrat")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ContractOfferDto> createContractOffer(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody ContractOfferUpdateDto offerDto);

	/**
	 * Met à jour une offre de contrat existante.
	 *
	 * @param id
	 *            Identifiant de l’offre à mettre à jour
	 * @param offerDto
	 *            DTO de mise à jour, validé selon {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec le {@link ContractOfferDto} mis à jour, ou
	 *         {@code 400 Bad Request} / {@code 409 Conflict} avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Mettre à jour un contrat")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ContractOfferDto> updateContractOffer(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody ContractOfferUpdateDto offerDto);

	/**
	 * Accepte une offre de contrat.
	 *
	 * @param contractOfferId
	 *            Identifiant de l’offre à accepter
	 * @return {@code 200 OK} avec le {@link ContractOfferDto} accepté, ou {@code 404 Not Found} si
	 *         introuvable
	 */
	@Operation(summary = "Accepter une offre de contrat")
	@PutMapping(value = "/{contractOfferId}/accept")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Offre de contrat acceptée", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "404", description = "Offre de contrat non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ContractOfferDto> acceptContractOffer(
			@ApiValidId @PathVariable("contractOfferId") Integer contractOfferId);

	/**
	 * Rejette une offre de contrat.
	 *
	 * @param contractOfferId
	 *            Identifiant de l’offre à rejeter
	 * @return {@code 200 OK} avec le {@link ContractOfferDto} rejeté, ou {@code 404 Not Found} si
	 *         introuvable
	 */
	@Operation(summary = "Rejeter une offre de contrat")
	@PutMapping(value = "/{contractOfferId}/reject")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Offre de contrat rejetée", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "404", description = "Offre de contrat non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ContractOfferDto> rejectContractOffer(
			@ApiValidId @PathVariable("contractOfferId") Integer contractOfferId);

	/**
	 * Liste toutes les offres de contrat, avec filtres facultatifs.
	 *
	 * @param traderId
	 *            (optionnel) ID du trader (vendeur ou acheteur)
	 * @param qualityId
	 *            (optionnel) ID de la qualité du contrat
	 * @param sellerId
	 *            (optionnel) ID du vendeur
	 * @param buyerId
	 *            (optionnel) ID de l’acheteur
	 * @return {@code 200 OK} avec la liste de {@link ContractOfferDto}
	 */
	@Operation(summary = "Obtenir tous les contrats")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ContractOfferDto.class))))})
	ResponseEntity<List<ContractOfferDto>> listContractOffers(
			@Parameter(description = "ID du trader pour filtrer les contratss") @RequestParam(value = "traderId", required = false) Integer traderId,
			@Parameter(description = "ID de la qualité") @RequestParam(value = "qualityId", required = false) Integer qualityId,
			@Parameter(description = "ID du vendeur") @RequestParam(value = "sellerId", required = false) Integer sellerId,
			@Parameter(description = "ID de l'acheteur") @RequestParam(value = "buyerId", required = false) Integer buyerId);

	/**
	 * Supprime une offre de contrat.
	 *
	 * @param id
	 *            Identifiant de l’offre à supprimer
	 * @return {@code 204 No Content} si la suppression réussit, ou {@code 404 Not Found} avec
	 *         {@link ApiErrorResponse}
	 */
	@Operation(summary = "Supprimer un contrat")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteContractOffer(@ApiValidId @PathVariable("id") Integer id);
}
