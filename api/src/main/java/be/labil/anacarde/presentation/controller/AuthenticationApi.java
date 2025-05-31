package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.presentation.payload.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Cette API offre un point d'accès pour l'authentification des utilisateurs.
 *
 * En cas de succès, un token JWT est généré et renvoyé sous forme de cookie HTTP-only, et le détail
 * de l'utilisateur est renvoyé dans le corps de la réponse.
 */
@Validated
@Tag(name = "Authentication", description = "API pour l'authentification des utilisateurs")
@RequestMapping("/api/auth")
public interface AuthenticationApi {

	/**
	 * Authentifie un utilisateur et renvoie un token JWT sous forme de cookie HTTP-only, ainsi que
	 * les détails de l'utilisateur authentifié dans le corps.
	 *
	 * @param loginRequest
	 *            Le payload contenant les identifiants (nom d'utilisateur et mot de passe).
	 * @param response
	 *            La réponse HTTP à laquelle le cookie JWT sera ajouté.
	 * @return Une ResponseEntity contenant un UserDetailDto si l'authentification est réussie, ou
	 *         un statut 401 sinon.
	 */
	@Operation(summary = "Authentifier l'utilisateur", description = "Authentifie un utilisateur et renvoie un token JWT "
			+ "sous forme de cookie HTTP-only ainsi que son UserDetailDto")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Utilisateur authentifié avec succès", content = @Content(schema = @Schema(implementation = UserDetailDto.class))),
			@ApiResponse(responseCode = "400", description = "Requête mal formée (loginRequest invalide)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Échec de l'authentification", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping("/signin")
	ResponseEntity<UserDetailDto> authenticateUser(
			@Parameter(description = "Identifiants de connexion", required = true, schema = @Schema(implementation = LoginRequest.class)) @NotNull(message = "Les identifiants de connexion sont obligatoires") @Valid @RequestBody LoginRequest loginRequest,
			HttpServletResponse response);

	/**
	 * Authentifie un compte utilisateur via Google.
	 *
	 * @param token
	 *            Le token ID de Google à valider, qui doit être non vide.
	 * @param response
	 *            La réponse HTTP à laquelle le cookie JWT sera ajouté.
	 * @return Le JWT applicatif
	 */
	@Operation(summary = "S’authentifier avec Google", description = "Vérifie l’ID-token Google, associe le compte Google si besoin, et renvoie un JWT.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Authentification réussie", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDetailDto.class))),
			@ApiResponse(responseCode = "400", description = "Token invalide ou données erronées", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class)))})
	@PostMapping(value = "/google", consumes = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<UserDetailDto> authenticateWithGoogle(
			@RequestBody @Validated({Default.class,
					ValidationGroups.Create.class}) @NotBlank String token,
			HttpServletResponse response) throws GeneralSecurityException, IOException;
	/**
	 * Renvoie les détails de l'utilisateur actuellement authentifié et génère un premier token csrf
	 * pour l'utilisateur.
	 *
	 * @param currentUser
	 *            L'utilisateur actuellement authentifié.
	 * @param request
	 *            La requête HTTP en cours.
	 * @param response
	 *            La réponse HTTP à laquelle le cookie JWT sera ajouté.
	 *
	 * @return Une ResponseEntity contenant un UserDetailDto si le client présente un cookie JWT
	 *         valide, ou 401 sinon.
	 */
	@Operation(summary = "Récupérer l'utilisateur courant", description = "Renvoie les détails (UserDetailDto) de l'utilisateur authentifié via le cookie JWT, cette méthode génère aussi un premier token csrf pour l'utilisateur")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Utilisateur authentifié", content = @Content(schema = @Schema(implementation = UserDetailDto.class))),
			@ApiResponse(responseCode = "401", description = "Utilisateur non authentifié", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),})
	@GetMapping("/me")
	ResponseEntity<UserDetailDto> getCurrentUser(@AuthenticationPrincipal User currentUser,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Déconnecte l'utilisateur en supprimant le cookie JWT côté client.
	 */
	@Operation(summary = "Déconnecter l'utilisateur", description = "Supprime le cookie JWT pour déconnecter l'utilisateur")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Utilisateur déconnecté avec succès", content = @Content())})
	@PostMapping("/signout")
	ResponseEntity<Void> logout(HttpServletResponse response);
}