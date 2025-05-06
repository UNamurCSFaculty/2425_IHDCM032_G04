package be.labil.anacarde.infrastructure.security;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorResponse;
import be.labil.anacarde.application.exception.ErrorDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
/**
 * Ce composant gère les erreurs d'accès refusé (403 Forbidden) dans une application Spring Security. Lorsqu'un
 * utilisateur tente d'accéder à une ressource pour laquelle il n'a pas les autorisations nécessaires, cette classe
 * envoie une réponse JSON contenant des détails sur l'erreur.
 */
public class RestAccessDeniedHandler implements AccessDeniedHandler {
	private final ObjectMapper mapper;
	/**
	 * Cette méthode est invoquée lorsqu'un utilisateur tente d'accéder à une ressource protégée sans les autorisations
	 * nécessaires. Elle envoie une réponse 403 Forbidden avec des détails sur l'erreur.
	 *
	 * @param req
	 *            La requête HTTP entrante.
	 * @param res
	 *            La réponse HTTP à envoyer.
	 * @param ex
	 *            L'exception d'accès refusé.
	 * @throws IOException
	 *             En cas d'erreur d'entrée/sortie lors de l'écriture de la réponse.
	 */
	@Override
	public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
		ApiErrorResponse body;

		if (ex instanceof MissingCsrfTokenException) {
			body = new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), LocalDateTime.now(), req.getRequestURI(),
					ApiErrorCode.ACCESS_FORBIDDEN_CSRF_MISSING.code(), List.of(new ErrorDetail(null,
							ApiErrorCode.ACCESS_FORBIDDEN_CSRF_MISSING.code(), "Accès refusé: token CSRF manquant.")));

		} else if (ex instanceof InvalidCsrfTokenException) {
			body = new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), LocalDateTime.now(), req.getRequestURI(),
					ApiErrorCode.ACCESS_FORBIDDEN_CSRF.code(), List.of(new ErrorDetail(null,
							ApiErrorCode.ACCESS_FORBIDDEN_CSRF.code(), "Accès refusé: token CSRF invalide.")));

		} else {
			body = new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), LocalDateTime.now(), req.getRequestURI(),
					ApiErrorCode.ACCESS_FORBIDDEN.code(), List.of(new ErrorDetail(null,
							ApiErrorCode.ACCESS_FORBIDDEN.code(), "Accès refusé : " + ex.getMessage())));
		}

		res.setStatus(HttpStatus.FORBIDDEN.value());
		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		mapper.writeValue(res.getOutputStream(), body);
	}
}
