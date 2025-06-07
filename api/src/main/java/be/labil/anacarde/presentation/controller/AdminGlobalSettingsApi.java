package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
import be.labil.anacarde.domain.dto.write.GlobalSettingsUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Points d’accès REST pour la gestion des réglages globaux du système.
 * <p>
 * Toutes les opérations requièrent une authentification JWT. Seules les opérations de mise à jour
 * sont réservées aux administrateurs.
 */
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/admin/global-settings", produces = "application/json")
@Tag(name = "admin")
public interface AdminGlobalSettingsApi {

	/**
	 * Récupère la configuration globale actuelle.
	 *
	 * @return ResponseEntity contenant un {@link GlobalSettingsDto} et un code HTTP 200 si trouvé,
	 *         ou un code HTTP 404 avec un corps {@link ApiErrorResponse} si les réglages ne sont
	 *         pas configurés.
	 */
	@Operation(summary = "Obtenir les réglages globaux")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Réglages globaux récupérés avec succès", content = @Content(schema = @Schema(implementation = GlobalSettingsDto.class))),
			@ApiResponse(responseCode = "404", description = "Réglages globaux non configurés", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<GlobalSettingsDto> getGlobalSettings();

	/**
	 * Met à jour les réglages globaux du système.
	 * <p>
	 * Seul un utilisateur avec le rôle ADMIN peut effectuer cette opération.
	 *
	 * @param globalSettingsUpdateDto
	 *            DTO contenant les nouvelles valeurs de configuration
	 * @return ResponseEntity contenant le {@link GlobalSettingsDto} mis à jour et un code HTTP 200
	 *         si la mise à jour réussit, ou un code HTTP 400 avec un corps {@link ApiErrorResponse}
	 *         en cas de données invalides.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Operation(summary = "Mettre à jour les réglages globaux")
	@PutMapping(consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Réglages globaux mis à jour avec succès", content = @Content(schema = @Schema(implementation = GlobalSettingsDto.class))),
			@ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<GlobalSettingsDto> updateGlobalSettings(
			@Valid @RequestBody GlobalSettingsUpdateDto globalSettingsUpdateDto);
}
