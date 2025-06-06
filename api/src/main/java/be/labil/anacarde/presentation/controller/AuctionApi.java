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
 * End-points « Enchères » (API de gestion des enchères). Permet de gérer les opérations relatives
 * aux enchères, telles que la création, la mise à jour, l'acceptation et la suppression d'enchères.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/auctions", produces = "application/json")
@Tag(name = "auctions", description = "Gestion des enchères")
public interface AuctionApi {

	@Operation(summary = "Obtenir une enchère")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = AuctionDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<AuctionDto> getAuction(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Obtenir les paramètres des enchères")
	@GetMapping("/settings")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = GlobalSettingsDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<GlobalSettingsDto> getAuctionSettings();

	@Operation(summary = "Créer une enchère")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = AuctionUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<AuctionDto> createAuction(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody AuctionUpdateDto auctionDto);

	@Operation(summary = "Mettre à jour une enchère")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = AuctionUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<AuctionDto> updateAuction(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody AuctionUpdateDto auctionDto);

	@Operation(summary = "Accepter une enchère")
	@PutMapping(value = "/{id}/accept")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = AuctionDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<AuctionDto> acceptAuction(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Obtenir toutes les enchères")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuctionDto.class))))})
	ResponseEntity<List<AuctionDto>> listAuctions(
			@Parameter(description = "ID du trader ayant créé les enchères") @RequestParam(value = "traderId", required = false) Integer traderId,
			@Parameter(description = "ID du trader ayant participé aux enchères") @RequestParam(value = "buyerId", required = false) Integer buyerId,
			@Parameter(description = "Status pour filtrer les enchères") @RequestParam(value = "status", required = false) String auctionStatus,
			@Parameter(description = "Nombre maximum d'enchères à obtenir") @RequestParam(value = "limit", required = false) Integer limit);

	@Operation(summary = "Supprimer une enchère")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteAuction(@ApiValidId @PathVariable("id") Integer id);
}
