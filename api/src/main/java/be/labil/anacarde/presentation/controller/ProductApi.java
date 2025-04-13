package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.ProductDto;
import be.labil.anacarde.domain.dto.ValidationGroups;
import be.labil.anacarde.presentation.controller.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/products", produces = "application/json")
@Tag(name = "products", description = "Opérations relatives aux produits")
public interface ProductApi {

	@Operation(summary = "Obtenir un produit")
	@GetMapping("/{id}")
	@ApiResponseGet
	ResponseEntity<? extends ProductDto> getProduct(@ApiValidId @PathVariable("id") Integer id);
	@Operation(summary = "Obtenir tous les produits")
	@ApiResponseGet
	@GetMapping
	ResponseEntity<List<? extends ProductDto>> listProducts(
			@Parameter(in = ParameterIn.QUERY, description = "ID du propriétaire des produits") @Valid @RequestParam(value = "traderId", required = false) Integer traderId);

	@Operation(summary = "Créer un produit")
	@ApiResponsePost
	@PostMapping
	ResponseEntity<? extends ProductDto> createProduct(
			@Validated({Default.class, ValidationGroups.Create.class}) @RequestBody ProductDto productDto);

	@Operation(summary = "Mettre à jour un produit")
	@ApiResponsePut
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<? extends ProductDto> updateProduct(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class, ValidationGroups.Update.class}) @RequestBody ProductDto productDto);

	@Operation(summary = "Supprimer un produit")
	@ApiResponseDelete
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteProduct(@ApiValidId @PathVariable("id") Integer id);
}
