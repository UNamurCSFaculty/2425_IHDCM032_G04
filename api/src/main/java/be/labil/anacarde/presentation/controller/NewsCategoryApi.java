package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.NewsCategoryDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * API pour la gestion des catégories d'articles de nouvelles.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/news/categories", produces = "application/json")
@Tag(name = "news")
public interface NewsCategoryApi {

	/**
	 * Crée une catégorie d'articles de nouvelles.
	 *
	 * @param newsCategoryDto
	 *            DTO pour la création de la catégorie.
	 * @return ResponseEntity avec la catégorie créée.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Operation(summary = "Créer une catégorie d'articles de nouvelles")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Catégorie d'articles de nouvelles créée", content = @Content(schema = @Schema(implementation = NewsCategoryDto.class))),
			@ApiResponse(responseCode = "400", description = "Entrée invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "La catégorie existe déjà", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<NewsCategoryDto> createNewsCategory(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody NewsCategoryDto newsCategoryDto);

	/**
	 * Récupère une catégorie d'articles de nouvelles par son ID.
	 *
	 * @param id
	 *            ID de la catégorie.
	 * @return ResponseEntity avec la catégorie.
	 */
	@Operation(summary = "Obtenir une catégorie d'articles de nouvelles par ID")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Opération réussie", content = @Content(schema = @Schema(implementation = NewsCategoryDto.class))),
			@ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<NewsCategoryDto> getNewsCategory(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Récupère toutes les catégories d'articles de nouvelles.
	 *
	 * @return ResponseEntity avec la liste des catégories.
	 */
	@Operation(summary = "Obtenir toutes les catégories d'articles de nouvelles")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste des catégories récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NewsCategoryDto.class))))})
	ResponseEntity<List<NewsCategoryDto>> listNewsCategories();

	/**
	 * Met à jour une catégorie d'articles de nouvelles.
	 *
	 * @param id
	 *            ID de la catégorie à mettre à jour.
	 * @param newsCategoryDto
	 *            DTO pour la mise à jour de la catégorie.
	 * @return ResponseEntity avec la catégorie mise à jour.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Operation(summary = "Mettre à jour une catégorie d'articles de nouvelles")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Catégorie d'articles de nouvelles mise à jour", content = @Content(schema = @Schema(implementation = NewsCategoryDto.class))),
			@ApiResponse(responseCode = "400", description = "Entrée invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Le nom de la catégorie existe déjà", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<NewsCategoryDto> updateNewsCategory(@ApiValidId @PathVariable("id") Integer id,
			@Validated({Default.class,
					ValidationGroups.Update.class}) @RequestBody NewsCategoryDto newsCategoryDto);

	/**
	 * Supprime une catégorie d'articles de nouvelles.
	 *
	 * @param id
	 *            ID de la catégorie à supprimer.
	 * @return ResponseEntity sans contenu.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Operation(summary = "Supprimer une catégorie d'articles de nouvelles")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Catégorie d'articles de nouvelles supprimée avec succès"),
			@ApiResponse(responseCode = "404", description = "Catégorie non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "La catégorie ne peut pas être supprimée (par exemple, en cours d'utilisation)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<Void> deleteNewsCategory(@ApiValidId @PathVariable("id") Integer id);
}