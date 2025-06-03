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
 * End-points « Réglages globaux » (API pour la gestion des réglages globaux du système).
 */
@SecurityRequirement(name = "jwt")
@RequestMapping(value = "/api/admin/global-settings", produces = "application/json")
@Tag(name = "admin-global-settings", description = "Gestion des réglages globaux du système")
public interface AdminGlobalSettingsApi {

	@Operation(summary = "Obtenir les réglages globaux")
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Réglages globaux récupérés avec succès", content = @Content(schema = @Schema(implementation = GlobalSettingsDto.class))),
			@ApiResponse(responseCode = "404", description = "Réglages globaux non configurés", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<GlobalSettingsDto> getGlobalSettings();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Operation(summary = "Mettre à jour les réglages globaux")
	@PutMapping(consumes = "application/json")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Réglages globaux mis à jour avec succès", content = @Content(schema = @Schema(implementation = GlobalSettingsDto.class))),
			@ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	ResponseEntity<GlobalSettingsDto> updateGlobalSettings(
			@Valid @RequestBody GlobalSettingsUpdateDto globalSettingsUpdateDto);
}
