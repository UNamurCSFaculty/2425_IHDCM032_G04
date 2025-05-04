package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.dto.ApplicationDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping(value = "/api/app", produces = "application/json")
@Tag(name = "application", description = "Récupération des données nécessaire à un client")
public interface ApplicationData {

	/**
	 * Récupère les données de l'application.
	 *
	 * @return Les données de l'application.
	 */
	@Operation(summary = "Récupère les données de l'application")
	@GetMapping(value = "")
	ApplicationDataDto getApplicationData();
}
