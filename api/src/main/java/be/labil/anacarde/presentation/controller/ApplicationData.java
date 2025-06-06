package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.ApplicationDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * End-point pour récupérer les données de l'application. Fournit des informations nécessaires à un
 * client, telles que la version de l'application, les paramètres de configuration, etc.
 */
@Validated
@RequestMapping(value = "/api/app", produces = "application/json")
@Tag(name = "application", description = "Gestion des données applicatives")
public interface ApplicationData {

	/**
	 * Récupère les données de l'application.
	 *
	 * @return Les données de l'application.
	 */
	@Operation(summary = "Récupère les données de l'application")
	@GetMapping(value = "")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ApplicationDataDto.class))),
			@ApiResponse(responseCode = "500", description = "", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	ApplicationDataDto getApplicationData();
}
