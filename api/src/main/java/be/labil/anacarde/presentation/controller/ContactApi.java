package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.write.ContactRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Contact", description = "API pour envoyer un message de contact")
@RequestMapping(value = "/api/contact", produces = "application/json")
public interface ContactApi {

	@Operation(summary = "Envoyer un message de contact", description = "Permet à un visiteur d'envoyer un message via le formulaire de contact.")
	@ApiResponses({@ApiResponse(responseCode = "200", description = "Message envoyé avec succès"),
			@ApiResponse(responseCode = "400", description = "Erreur de validation", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping(consumes = "application/json")
	ResponseEntity<Void> sendContactMessage(
			@Parameter(description = "Payload du message de contact", required = true) @Valid @RequestBody ContactRequestDto contactRequestDto);
}
