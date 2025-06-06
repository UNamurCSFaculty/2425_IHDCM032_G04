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
 * End-points « Contrats » (API de gestion des contrats). Permet de gérer les opérations relatives
 * aux contrats, telles que la création, la mise à jour, l'acceptation et la suppression de
 * contrats.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/contracts", produces = "application/json")
@Tag(name = "contracts", description = "Opérations relatives aux contrats")
public interface ContractOfferApi {

	@Operation(summary = "Obtenir un contrat")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<ContractOfferDto> getContractOffer(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un contrat")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ContractOfferDto> createContractOffer(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody ContractOfferUpdateDto storeDetailDto);

	@Operation(summary = "Mettre à jour un contrat")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ContractOfferDto> updateContractOffer(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody ContractOfferUpdateDto storeDetailDto);

	@Operation(summary = "Accepter une offre de contrat")
	@PutMapping(value = "/{contractOfferId}/accept")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Offre de contrat acceptée", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "404", description = "Offre de contrat non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ContractOfferDto> acceptContractOffer(
			@ApiValidId @PathVariable("contractOfferId") Integer contractOfferId);

	@Operation(summary = "Rejeter une offre de contrat")
	@PutMapping(value = "/{contractOfferId}/reject")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Offre de contrat rejetée", content = @Content(schema = @Schema(implementation = ContractOfferDto.class))),
			@ApiResponse(responseCode = "404", description = "Offre de contrat non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<ContractOfferDto> rejectContractOffer(
			@ApiValidId @PathVariable("contractOfferId") Integer contractOfferId);

	@Operation(summary = "Obtenir tous les contrats")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ContractOfferDto.class))))})
	ResponseEntity<List<ContractOfferDto>> listContractOffers(
			@Parameter(description = "ID du trader pour filtrer les contratss") @RequestParam(value = "traderId", required = false) Integer traderId,
			@Parameter(description = "ID de la qualité") @RequestParam(value = "qualityId", required = false) Integer qualityId,
			@Parameter(description = "ID du vendeur") @RequestParam(value = "sellerId", required = false) Integer sellerId,
			@Parameter(description = "ID de l'acheteur") @RequestParam(value = "buyerId", required = false) Integer buyerId);

	@Operation(summary = "Supprimer un contrat")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteContractOffer(@ApiValidId @PathVariable("id") Integer id);
}
