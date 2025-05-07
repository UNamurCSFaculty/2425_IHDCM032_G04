package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.LanguageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Cette API offre des points d'accès permettant de récupérer, créer, mettre à jour et supprimer des langues.
 */
@Validated
@Tag(name = "Languages", description = "API de gestion des langues")
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/languages", produces = "application/json")
public interface LanguageApi {

	/**
	 * Récupère les informations détaillées d'une langue à partir de son identifiant.
	 */
	@Operation(summary = "Récupérer les détails d'une langue", description = "Renvoie les informations détaillées d'une langue à partir de son ID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Langue trouvée", content = @Content(schema = @Schema(implementation = LanguageDto.class))),
			@ApiResponse(responseCode = "404", description = "Langue non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@GetMapping("/{id}")
	ResponseEntity<LanguageDto> getLanguage(
			@NotNull(message = "L'ID de la langue est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant de la langue", example = "1", required = true) @PathVariable("id") Integer id);

	/**
	 * Crée une nouvelle langue dans le système.
	 */
	@Operation(summary = "Créer une langue", description = "Crée une nouvelle langue dans le système.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Langue créée avec succès", content = @Content(schema = @Schema(implementation = LanguageDto.class))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping(consumes = "application/json")
	ResponseEntity<LanguageDto> createLanguage(@Valid @RequestBody LanguageDto languageDto);

	/**
	 * Renvoie la liste de toutes les langues présentes dans le système.
	 */
	@Operation(summary = "Lister toutes les langues", description = "Renvoie la liste de toutes les langues présentes dans le système.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LanguageDto.class))))})
	@GetMapping
	ResponseEntity<List<LanguageDto>> listLanguages();

	/**
	 * Met à jour une langue existante en utilisant l'ID spécifié dans l'URL.
	 */
	@Operation(summary = "Mettre à jour une langue", description = "Met à jour une langue existante en utilisant l'ID spécifié dans l'URL.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Langue mise à jour avec succès", content = @Content(schema = @Schema(implementation = LanguageDto.class))),
			@ApiResponse(responseCode = "400", description = "Erreur de validation ou JSON invalide", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Langue non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PutMapping(value = "/{id}", consumes = "application/json")
	ResponseEntity<LanguageDto> updateLanguage(
			@NotNull(message = "L'ID de la langue est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant de la langue", example = "1", required = true) @PathVariable("id") Integer id,
			@Valid @RequestBody LanguageDto languageDto);

	/**
	 * Supprime une langue à partir de son identifiant.
	 */
	@Operation(summary = "Supprimer une langue", description = "Supprime une langue en fonction de son ID.")
	@ApiResponses({@ApiResponse(responseCode = "204", description = "Langue supprimée avec succès"),
			@ApiResponse(responseCode = "404", description = "Langue non trouvée", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	ResponseEntity<Void> deleteLanguage(
			@NotNull(message = "L'ID de la langue est obligatoire") @Positive(message = "L'ID doit être un entier positif") @Parameter(description = "Identifiant de la langue", example = "1", required = true) @PathVariable("id") Integer id);

}
