package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.write.product.ProductUpdateDto;
import be.labil.anacarde.presentation.controller.annotations.ApiValidId;
import be.labil.anacarde.presentation.controller.enums.ProductType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * API pour la gestion des produits. Permet de gérer les Gestion des produits, telles que la
 * création, la mise à jour, la suppression et la récupération des produits.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/products", produces = "application/json")
@Tag(name = "products", description = "Gestion des produits")
public interface ProductApi {
	@Operation(summary = "Obtenir un produit")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ProductDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<? extends ProductDto> getProduct(@ApiValidId @PathVariable("id") Integer id);

	@Operation(summary = "Obtenir tous les produits")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductDto.class))))})
	ResponseEntity<List<? extends ProductDto>> listProducts(
			@Parameter(in = ParameterIn.QUERY, description = "ID du propriétaire des produits") @Valid @RequestParam(value = "traderId", required = false) Integer traderId,
			@Parameter(in = ParameterIn.QUERY, description = "Type du produit", schema = @Schema(implementation = ProductType.class)) @RequestParam(value = "productType", required = false) ProductType productType);

	@Operation(summary = "Créer un produit")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = ProductDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<? extends ProductDto> createProduct(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody ProductUpdateDto productDto);

	@Operation(summary = "Mettre à jour un produit")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = ProductDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<? extends ProductDto> updateProduct(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody ProductUpdateDto productDto);

	@Operation(summary = "Supprimer un produit")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteProduct(@ApiValidId @PathVariable("id") Integer id);
}
