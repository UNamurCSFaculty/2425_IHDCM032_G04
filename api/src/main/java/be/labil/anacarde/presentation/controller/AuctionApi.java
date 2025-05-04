package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.AuctionDto;
import be.labil.anacarde.domain.dto.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping(value = "/api/auctions", produces = "application/json")
@Tag(name = "auctions", description = "Opérations relatives aux enchères")
public interface AuctionApi {

	@Operation(summary = "Obtenir une enchère")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<AuctionDto> getAuction(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer une enchère")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<AuctionDto> createAuction(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody AuctionDto auctionDto);

	@Operation(summary = "Mettre à jour une enchère")
	@ApiResponsePut
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<AuctionDto> updateAuction(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody AuctionDto auctionDto);

	@Operation(summary = "Obtenir toutes les enchères")
	@ApiResponse(responseCode = "200", description = "Liste des enchères", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuctionDto.class))))
	@GetMapping
	ResponseEntity<List<AuctionDto>> listAuctions(
			@Parameter(description = "ID du trader pour filtrer les enchères", required = false) @RequestParam(value = "traderId", required = false) Integer traderId);

	@Operation(summary = "Supprimer une enchère")
	@ApiResponseDelete
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteAuction(@ApiValidId @PathVariable("id") Integer id);
}
