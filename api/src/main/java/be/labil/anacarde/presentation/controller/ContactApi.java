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

/**
 * Interface REST pour l’envoi de messages de contact via le formulaire public.
 * <p>
 * Permet à un visiteur non authentifié d’envoyer un message de contact qui sera traité par le
 * service de support (envoi d’email, stockage, etc.).
 */
@Tag(name = "contact", description = "Gestion des messages de contact")
@RequestMapping(value = "/api/contact", produces = "application/json")
public interface ContactApi {

	/**
	 * Envoie un message de contact.
	 * <p>
	 * Valide le payload du message, puis le transmet au service de traitement (par exemple envoi
	 * d’email ou persistance en base).
	 *
	 * @param contactRequestDto
	 *            DTO contenant :
	 *            <ul>
	 *            <li>{@code name} : nom de l’expéditeur (requis)</li>
	 *            <li>{@code email} : adresse email de l’expéditeur (requis, format valide)</li>
	 *            <li>{@code subject} : objet du message (requis)</li>
	 *            <li>{@code message} : contenu du message (requis)</li>
	 *            </ul>
	 * @return {@code 200 OK} si le message a été envoyé avec succès.
	 * @throws org.springframework.web.bind.MethodArgumentNotValidException
	 *             en cas de violation des contraintes de validation du DTO
	 *             ({@code 400 Bad Request}).
	 * @throws RuntimeException
	 *             en cas d’erreur interne du serveur ({@code 500 Internal Server Error}).
	 */
	@Operation(summary = "Envoyer un message de contact", description = "Permet à un visiteur d'envoyer un message via le formulaire de contact.")
	@ApiResponses({@ApiResponse(responseCode = "200", description = "Message envoyé avec succès"),
			@ApiResponse(responseCode = "400", description = "Erreur de validation", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping(consumes = "application/json")
	ResponseEntity<Void> sendContactMessage(
			@Parameter(description = "Payload du message de contact", required = true) @Valid @RequestBody ContactRequestDto contactRequestDto);
}
