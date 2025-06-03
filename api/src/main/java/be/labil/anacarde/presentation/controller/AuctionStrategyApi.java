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
 * End-points « Stratégies d'enchères » (API pour la gestion des stratégies d'enchères). Permet de
 * gérer les opérations relatives aux stratégies d'enchères, telles que la création, la mise à jour,
 * la suppression et la récupération des stratégies.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/auctions/strategies", produces = "application/json")
@Tag(name = "auction-strategies", description = "Opérations relatives aux stratégies d'enchères")
public interface AuctionStrategyApi {

	@Operation(summary = "Obtenir une stratégie d'enchère")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = AuctionStrategyDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<AuctionStrategyDto> getAuctionStrategy(
			@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer une stratégie d'enchère")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = AuctionStrategyDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<AuctionStrategyDto> createAuctionStrategy(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody AuctionStrategyDto auctionStrategyDto);

	@Operation(summary = "Mettre à jour une stratégie d'enchère")
	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = AuctionStrategyDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<AuctionStrategyDto> updateAuctionStrategy(
			@ApiValidId @PathVariable("id") Integer id, @Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody AuctionStrategyDto auctionStrategyDto);

	@Operation(summary = "Obtenir toutes les stratégies d'enchère")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuctionStrategyDto.class))))})
	ResponseEntity<List<AuctionStrategyDto>> listAuctionStrategies();

	@Operation(summary = "Supprimer une stratégie d'enchère")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteAuctionStrategy(@ApiValidId @PathVariable("id") Integer id);
}
