package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.BidDto;
import be.labil.anacarde.domain.dto.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/auctions/{auctionId}/bids/", produces = "application/json")
@Tag(name = "auctions", description = "Opérations relatives aux offres")
public interface BidApi {

	@Operation(summary = "Obtenir une offre")
	@GetMapping("/{bidId}")
	@ApiResponseGet
	ResponseEntity<BidDto> getBid(@ApiValidId @PathVariable("bidId") Integer bidId);

	@Operation(summary = "Créer une offre")
	@ApiResponsePost
	@PostMapping
	ResponseEntity<BidDto> createBid(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody BidDto bidDto);

	@Operation(summary = "Mettre à jour une offre")
	@ApiResponsePut
	@PutMapping(value = "/{bidId}", consumes = "application/json")
	ResponseEntity<BidDto> updateBid(@ApiValidId @PathVariable("bidId") Integer bidId,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody BidDto bidDto);

	@Operation(summary = "Obtenir toutes les offres")
	@ApiResponseGet
	// @ApiResponse(responseCode = "200", description = "Liste des enchères", content = @Content(mediaType =
	// "application/json", array = @ArraySchema(schema = @Schema(implementation = BidDto.class))))
	@GetMapping
	ResponseEntity<List<BidDto>> listBids(@ApiValidId @PathVariable("auctionId") Integer auctionId);

	@Operation(summary = "Supprimer une offre")
	@ApiResponseDelete
	@DeleteMapping("/{bidId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteBid(@ApiValidId @PathVariable("bidId") Integer bidId);
}
