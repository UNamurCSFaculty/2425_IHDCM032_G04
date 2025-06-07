package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
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
 * Interface REST pour la gestion des enchères.
 * <p>
 * Définit les opérations CRUD et métiers suivantes :
 * <ul>
 * <li>Récupérer une enchère par son ID.</li>
 * <li>Récupérer la configuration des enchères globales.</li>
 * <li>Créer une nouvelle enchère.</li>
 * <li>Mettre à jour une enchère existante.</li>
 * <li>Accepter (clore) une enchère.</li>
 * <li>Lister les enchères avec filtres facultatifs (créateur, participant, statut).</li>
 * <li>Supprimer (désactiver) une enchère.</li>
 * </ul>
 * Toutes les méthodes sont sécurisées par JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/auctions", produces = "application/json")
@Tag(name = "auctions", description = "Gestion des enchères")
public interface AuctionApi {

	/**
	 * Récupère une enchère par son identifiant.
	 *
	 * @param id
	 *            Identifiant de l’enchère (doit être positif, non null)
	 * @return {@code 200 OK} avec l’{@link AuctionDto}, ou {@code 404 Not Found} avec
	 *         {@link ApiErrorResponse}
	 */
	@Operation(summary = "Obtenir une enchère")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = AuctionDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<AuctionDto> getAuction(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Récupère les réglages globaux applicables aux enchères.
	 *
	 * @return {@code 200 OK} avec le {@link GlobalSettingsDto}, ou {@code 404 Not Found} si non
	 *         configuré, avec {@link ApiErrorResponse}
	 */
	@Operation(summary = "Obtenir les paramètres des enchères")
	@GetMapping("/settings")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = GlobalSettingsDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<GlobalSettingsDto> getAuctionSettings();

	/**
	 * Crée une nouvelle enchère.
	 *
	 * @param auctionDto
	 *            données de création de l’enchère validées selon {@link ValidationGroups.Create}
	 * @return {@code 201 Created} avec l’{@link AuctionDto}, ou {@code 400 Bad Request} en cas de
	 *         données invalides, ou {@code 409 Conflict} si une enchère conflictuelle existe
	 */
	@Operation(summary = "Créer une enchère")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = AuctionUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<AuctionDto> createAuction(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody AuctionUpdateDto auctionDto);

	/**
	 * Met à jour une enchère existante.
	 *
	 * @param id
	 *            Identifiant de l’enchère à mettre à jour
	 * @param auctionDto
	 *            données de mise à jour validées selon {@link ValidationGroups.Update}
	 * @return {@code 200 OK} avec l’{@link AuctionDto}, ou {@code 400 Bad Request} ou
	 *         {@code 409 Conflict}
	 */
	@Operation(summary = "Mettre à jour une enchère")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = AuctionUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<AuctionDto> updateAuction(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody AuctionUpdateDto auctionDto);

	/**
	 * Accepte (clôture) une enchère.
	 *
	 * @param id
	 *            Identifiant de l’enchère à accepter
	 * @return {@code 200 OK} avec l’{@link AuctionDto}, ou {@code 404 Not Found} si introuvable
	 */
	@Operation(summary = "Accepter une enchère")
	@PutMapping(value = "/{id}/accept")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = AuctionDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<AuctionDto> acceptAuction(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Liste toutes les enchères avec filtres facultatifs.
	 *
	 * @param traderId
	 *            (optionnel) ID du créateur des enchères
	 * @param buyerId
	 *            (optionnel) ID d’un participant aux enchères
	 * @param auctionStatus
	 *            (optionnel) statut pour filtrer les enchères
	 * @param limit
	 *            (optionnel) nombre maximum de résultats
	 * @return {@code 200 OK} avec la liste des {@link AuctionDto}
	 */
	@Operation(summary = "Obtenir toutes les enchères")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuctionDto.class))))})
	ResponseEntity<List<AuctionDto>> listAuctions(
			@Parameter(description = "ID du trader ayant créé les enchères") @RequestParam(value = "traderId", required = false) Integer traderId,
			@Parameter(description = "ID du trader ayant participé aux enchères") @RequestParam(value = "buyerId", required = false) Integer buyerId,
			@Parameter(description = "Status pour filtrer les enchères") @RequestParam(value = "status", required = false) String auctionStatus,
			@Parameter(description = "Nombre maximum d'enchères à obtenir") @RequestParam(value = "limit", required = false) Integer limit);

	/**
	 * Supprime (désactive) une enchère.
	 *
	 * @param id
	 *            Identifiant de l’enchère à supprimer
	 */
	@Operation(summary = "Supprimer une enchère")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteAuction(@ApiValidId @PathVariable("id") Integer id);
}
