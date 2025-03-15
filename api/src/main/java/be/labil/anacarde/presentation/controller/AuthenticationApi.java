package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.presentation.payload.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Cette API fournit un point d'accès pour l'authentification des utilisateurs.
 *
 * <p>
 * À la réussite de l'authentification, un token JWT est généré et renvoyé sous forme de cookie HTTP-only.
 */
@Validated
@Tag(name = "Authentication", description = "API pour l'authentification des utilisateurs")
@RequestMapping("/api/auth")
public interface AuthenticationApi {

	/**
	 * Authentifie un utilisateur et renvoie un token JWT sous forme de cookie HTTP-only.
	 *
	 * @param loginRequest
	 *            Le payload contenant les identifiants (nom d'utilisateur et mot de passe).
	 * @param response
	 *            La réponse HTTP à laquelle le cookie JWT sera ajouté.
	 * @return Une ResponseEntity avec un message de succès si l'authentification est réussie, ou un message d'erreur
	 *         sinon.
	 */
	@Operation(summary = "Authentifier l'utilisateur", description = "Authentifie un utilisateur et renvoie un token JWT sous forme de cookie HTTP-only")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Utilisateur authentifié avec succès", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "401", description = "Échec de l'authentification", content = @Content(schema = @Schema(implementation = String.class)))})
	@PostMapping("/signin")
	ResponseEntity<?> authenticateUser(
			@NotNull(message = "Les identifiants de connexion sont obligatoires") @Valid @RequestBody @Parameter(description = "Identifiants de connexion", required = true, schema = @Schema(implementation = LoginRequest.class)) LoginRequest loginRequest,
			HttpServletResponse response);
}
