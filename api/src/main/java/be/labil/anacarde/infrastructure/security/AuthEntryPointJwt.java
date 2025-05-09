package be.labil.anacarde.infrastructure.security;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.application.exception.ErrorDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Ce composant est responsable de l'envoi d'une réponse d'erreur 401 Unauthorized lorsqu'une
 * défaillance d'authentification survient.
 */
@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private final ObjectMapper mapper;

	/**
	 * Cette méthode est invoquée lorsqu'une exception d'authentification est levée, indiquant que
	 * l'utilisateur n'est pas autorisé à accéder à la ressource demandée. Elle envoie une erreur
	 * 401 Unauthorized accompagnée d'un message d'erreur.
	 *
	 * @param req
	 *            La HttpServletRequest dans laquelle la tentative d'authentification a été
	 *            effectuée.
	 * @param res
	 *            La HttpServletResponse utilisée pour envoyer la réponse d'erreur.
	 * @param authEx
	 *            L'exception qui a déclenché cette défaillance d'authentification.
	 * @throws IOException
	 *             En cas d'erreur d'entrée ou de sortie lors du traitement de la réponse.
	 * @throws ServletException
	 *             En cas d'erreur spécifique au servlet pendant le traitement.
	 */
	@Override
	public void commence(HttpServletRequest req, HttpServletResponse res,
			AuthenticationException authEx) throws IOException, ServletException {
		// 401 Unauthorized
		ApiErrorResponse body = new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(),
				LocalDateTime.now(), req.getRequestURI(), ApiErrorCode.ACCESS_UNAUTHORIZED.code(),
				List.of(new ErrorDetail(null, ApiErrorCode.ACCESS_UNAUTHORIZED.code(),
						authEx.getMessage())));
		res.setStatus(HttpStatus.UNAUTHORIZED.value());
		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		mapper.writeValue(res.getOutputStream(), body);
	}
}
