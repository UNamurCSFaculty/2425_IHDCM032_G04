package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.ContractOfferDto;
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
@RequestMapping(value = "/api/contracts", produces = "application/json")
@Tag(name = "contracts", description = "Opérations relatives aux contrats")
public interface ContractOfferApi {

	@Operation(summary = "Obtenir un contrat")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<ContractOfferDto> getContractOffer(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Créer un contrat")
	@PostMapping
	@ApiResponsePost
	ResponseEntity<ContractOfferDto> createContractOffer(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody ContractOfferDto storeDetailDto);

	@Operation(summary = "Mettre à jour un contrat")
	@ApiResponsePut
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<ContractOfferDto> updateContractOffer(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody ContractOfferDto storeDetailDto);

	@Operation(summary = "Obtenir tous les contrats")
	@ApiResponseGet
	@GetMapping
	ResponseEntity<List<ContractOfferDto>> listContractOffers();

	@Operation(summary = "Supprimer un contrat")
	@ApiResponseDelete
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteContractOffer(@ApiValidId @PathVariable("id") Integer id);
}
