package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.AuctionStrategyDto;
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
@RequestMapping(value = "/api/auctions/strategies", produces = "application/json")
@Tag(name = "auction-strategies", description = "Opérations relatives aux stratégies d'enchères")
public interface AuctionStrategyApi {

	@Operation(summary = "Obtenir une stratégie d'enchère")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<? extends AuctionStrategyDto> getAuctionStrategy(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer une stratégie d'enchère")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<? extends AuctionStrategyDto> createAuctionStrategy(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody AuctionStrategyDto auctionStrategyDto);

	@Operation(summary = "Mettre à jour une stratégie d'enchère")
	@PutMapping("/{id}")
	@ApiResponsePut
	ResponseEntity<? extends AuctionStrategyDto> updateAuctionStrategy(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody AuctionStrategyDto auctionStrategyDto);

	@Operation(summary = "Obtenir toutes les stratégies d'enchère")
	@GetMapping
	@ApiResponseGet
	ResponseEntity<List<? extends AuctionStrategyDto>> listAuctionStrategies();

	@Operation(summary = "Supprimer une stratégie d'enchère")
	@DeleteMapping("/{id}")
	@ApiResponseDelete
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteAuctionStrategy(@ApiValidId @PathVariable("id") Integer id);
}
