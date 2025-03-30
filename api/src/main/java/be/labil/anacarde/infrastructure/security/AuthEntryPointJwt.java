package be.labil.anacarde.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Ce composant est responsable de l'envoi d'une réponse d'erreur 401 Unauthorized lorsqu'une défaillance
 * d'authentification survient.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	/**
	 * Cette méthode est invoquée lorsqu'une exception d'authentification est levée, indiquant que l'utilisateur n'est
	 * pas autorisé à accéder à la ressource demandée. Elle envoie une erreur 401 Unauthorized accompagnée d'un message
	 * d'erreur.
	 *
	 * @param request
	 *            La HttpServletRequest dans laquelle la tentative d'authentification a été effectuée.
	 * @param response
	 *            La HttpServletResponse utilisée pour envoyer la réponse d'erreur.
	 * @param authException
	 *            L'exception qui a déclenché cette défaillance d'authentification.
	 * @throws IOException
	 *             En cas d'erreur d'entrée ou de sortie lors du traitement de la réponse.
	 * @throws ServletException
	 *             En cas d'erreur spécifique au servlet pendant le traitement.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// 401 Unauthorized
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erreur : Non autorisé");
	}
}
