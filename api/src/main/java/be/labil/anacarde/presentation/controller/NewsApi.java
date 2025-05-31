package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.NewsDto;
import be.labil.anacarde.domain.dto.db.NewsPageDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.write.NewsCreateDto;
import be.labil.anacarde.domain.dto.write.NewsFilterDto;
import be.labil.anacarde.domain.dto.write.NewsUpdateDto;
import be.labil.anacarde.presentation.controller.annotations.ApiValidId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * API pour la gestion des articles de nouvelles.
 */
@Validated
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/news", produces = "application/json")
@Tag(name = "news", description = "Opérations relatives aux articles de nouvelles")
public interface NewsApi {

	/**
	 * Crée un article de nouvelles.
	 *
	 * @param newsDto
	 *            DTO pour la création de l'article de nouvelles.
	 * @return ResponseEntity avec l'article de nouvelles créé.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Operation(summary = "Créer un article de nouvelles")
	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Article de nouvelles créé", content = @Content(schema = @Schema(implementation = NewsCreateDto.class))),
			@ApiResponse(responseCode = "400", description = "Entrée invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<NewsDto> createNews(@Validated({Default.class,
			ValidationGroups.Create.class}) @RequestBody NewsCreateDto newsDto);

	/**
	 * Récupère un article de nouvelles par son ID.
	 *
	 * @param id
	 *            ID de l'article de nouvelles.
	 * @return ResponseEntity avec l'article de nouvelles.
	 */
	@Operation(summary = "Obtenir un article de nouvelles par ID")
	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Opération réussie", content = @Content(schema = @Schema(implementation = NewsDto.class))),
			@ApiResponse(responseCode = "404", description = "Article de nouvelles non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<NewsDto> getNews(@ApiValidId @PathVariable("id") Integer id);

	/**
	 * Récupère tous les articles de nouvelles avec pagination et filtrage.
	 *
	 * @param requestDto
	 *            DTO pour les paramètres de la requête de liste.
	 * @param pageable
	 *            Informations de pagination.
	 * @return ResponseEntity avec une page d'articles de nouvelles.
	 */
	@Operation(summary = "Obtenir tous les articles de nouvelles avec pagination et filtrage")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste des articles de nouvelles récupérée avec succès", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NewsPageDto.class)))})
	ResponseEntity<NewsPageDto> listNews(@ParameterObject @Valid NewsFilterDto requestDto,
			@ParameterObject @PageableDefault(size = 10, sort = "publicationDate", direction = Sort.Direction.DESC) Pageable pageable);

	/**
	 * Met à jour un article existant.
	 *
	 * @param id
	 *            ID de l'article de nouvelles à mettre à jour.
	 * @param newsDto
	 *            DTO pour la mise à jour de l'article de nouvelles.
	 * @return ResponseEntity avec l'article de nouvelles mis à jour.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Operation(summary = "Mettre à jour un article de nouvelles existant")
	@PutMapping(value = "/{id}", consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Article de nouvelles mis à jour", content = @Content(schema = @Schema(implementation = NewsUpdateDto.class))),
			@ApiResponse(responseCode = "400", description = "Entrée invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Article de nouvelles non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<NewsDto> updateNews(@ApiValidId @PathVariable("id") Integer id, @Validated({
			Default.class, ValidationGroups.Update.class}) @RequestBody NewsUpdateDto newsDto);

	/**
	 * Supprime un article de nouvelles.
	 *
	 * @param id
	 *            ID de l'article de nouvelles à supprimer.
	 * @return ResponseEntity sans contenu.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Operation(summary = "Supprimer un article de nouvelles")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Article de nouvelles supprimé avec succès"),
			@ApiResponse(responseCode = "404", description = "Article de nouvelles non trouvé", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<Void> deleteNews(@ApiValidId @PathVariable("id") Integer id);
}