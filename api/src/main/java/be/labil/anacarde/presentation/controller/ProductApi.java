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
 * API REST pour la gestion des produits.
 * <p>
 * Fournit les opérations CRUD suivantes :
 * <ul>
 * <li>Récupérer un produit par son ID (brut ou transformé).</li>
 * <li>Lister tous les produits, avec filtres facultatifs par propriétaire et type.</li>
 * <li>Créer un nouveau produit (harvest ou transformed).</li>
 * <li>Mettre à jour un produit existant.</li>
 * <li>Supprimer un produit par son ID.</li>
 * </ul>
 * Toutes les méthodes sont sécurisées par JWT.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/products", produces = "application/json")
@Tag(name = "products", description = "Gestion des produits")
public interface ProductApi {
	/**
	 * Récupère un produit par son identifiant.
	 *
	 * @param id
	 *            Identifiant du produit (doit être un entier positif).
	 * @return {@code 200 OK} avec un {@link ProductDto} (harvest ou transformed), ou
	 *         {@code 404 Not Found} avec {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Obtenir un produit")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ProductDto.class))),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<? extends ProductDto> getProduct(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Liste tous les produits, avec options de filtrage.
	 *
	 * @param traderId
	 *            (optionnel) Identifiant du propriétaire des produits.
	 * @param productType
	 *            (optionnel) Type de produit à filtrer (harvest ou transformed).
	 * @return {@code 200 OK} avec la liste de {@link ProductDto}, éventuellement vide.
	 */
	@Operation(summary = "Obtenir tous les produits")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductDto.class))))})
	ResponseEntity<List<? extends ProductDto>> listProducts(
			@Parameter(in = ParameterIn.QUERY, description = "ID du propriétaire des produits") @Valid @RequestParam(value = "traderId", required = false) Integer traderId,
			@Parameter(in = ParameterIn.QUERY, description = "Type du produit", schema = @Schema(implementation = ProductType.class)) @RequestParam(value = "productType", required = false) ProductType productType);

	/**
	 * Crée un nouveau produit.
	 *
	 * @param productDto
	 *            DTO de création, validé selon {@link ValidationGroups.Create}.
	 * @return {@code 201 Created} avec le {@link ProductDto} créé, ou {@code 400 Bad Request} /
	 *         {@code 409 Conflict} avec {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Créer un produit")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = ProductDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<? extends ProductDto> createProduct(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody ProductUpdateDto productDto);

	/**
	 * Met à jour un produit existant.
	 *
	 * @param id
	 *            Identifiant du produit à mettre à jour.
	 * @param productDto
	 *            DTO de mise à jour, validé selon {@link ValidationGroups.Update}.
	 * @return {@code 200 OK} avec le {@link ProductDto} mis à jour, ou {@code 400 Bad Request} /
	 *         {@code 409 Conflict} avec {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Mettre à jour un produit")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "", content = @Content(schema = @Schema(implementation = ProductDto.class))),
			@ApiResponse(responseCode = "400", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<? extends ProductDto> updateProduct(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody ProductUpdateDto productDto);

	/**
	 * Supprime un produit par son identifiant.
	 *
	 * @param id
	 *            Identifiant du produit à supprimer.
	 * @return {@code 204 No Content} si la suppression réussit, ou {@code 404 Not Found} avec
	 *         {@link ApiErrorResponse}.
	 */
	@Operation(summary = "Supprimer un produit")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema())),
			@ApiResponse(responseCode = "404", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ResponseEntity<Void> deleteProduct(@ApiValidId @PathVariable("id") Integer id);
}
