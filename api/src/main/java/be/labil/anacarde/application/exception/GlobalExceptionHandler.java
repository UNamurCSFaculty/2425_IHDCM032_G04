package be.labil.anacarde.application.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
/**
 * Cette classe gère les exceptions levées dans l'application et les traduit en réponses HTTP significatives. Elle prend
 * en charge les erreurs de validation, les exceptions de ressource non trouvée, les violations d'intégrité des données,
 * les erreurs de lecture des messages HTTP, les conflits d'accès concurrent, ainsi que les exceptions génériques.
 */
public class GlobalExceptionHandler {

	private final MessageSource messageSource;

	/**
	 * Constructeur de GlobalExceptionHandler.
	 *
	 * @param messageSource
	 *            La source de messages utilisée pour récupérer les messages localisés.
	 */
	public GlobalExceptionHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP et un message d'erreur détaillé.
	 *
	 * @param request
	 *            La requête HTTP contenant les détails de la requête.
	 * @param code
	 *            Le code d'erreur à inclure dans la réponse.
	 * @param errors
	 *            La liste des erreurs détaillées à inclure dans la réponse.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec le message d'erreur
	 */
	private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String code, List<ErrorDetail> errors,
			HttpServletRequest request) {
		ApiErrorResponse body = new ApiErrorResponse(status.value(), LocalDateTime.now(), request.getRequestURI(), code,
				errors);
		return ResponseEntity.status(status).body(body);
	}

	/**
	 * Récupère toutes les erreurs de validation contenues dans l'exception et les retourne sous forme d'un objet
	 * ValidationErrorResponse.
	 *
	 * @param ex
	 *            L'exception MethodArgumentNotValidException contenant les erreurs de validation.
	 * @return Une ResponseEntity contenant un objet ValidationErrorResponse détaillant les erreurs et le statut HTTP
	 *         BAD_REQUEST.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> new ErrorDetail(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
		return buildResponse(HttpStatus.BAD_REQUEST, "validation.error", details, request);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP NOT_FOUND lorsqu'une ressource demandée est introuvable.
	 *
	 * @param ex
	 *            L'exception ResourceNotFoundException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec le message de l'exception et le statut HTTP
	 *         NOT_FOUND.
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.NOT_FOUND, "resource.not_found",
				List.of(new ErrorDetail(null, ex.getMessage())), request);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP BAD_REQUEST lorsqu'une exception BadRequestException est levée.
	 * Cela peut être dû à des paramètres manquants ou invalides dans la requête.
	 *
	 * @param ex
	 *            L'exception BadRequestException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec le message de l'exception et le statut HTTP
	 *         BAD_REQUEST.
	 */
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, "bad_request", List.of(new ErrorDetail(null, ex.getMessage())),
				request);
	}

	/**
	 * Extrait un code d'erreur à partir de la cause racine de l'exception, récupère un message localisé et retourne une
	 * réponse d'erreur avec le statut HTTP CONFLICT.
	 *
	 * @param ex
	 *            L'exception DataIntegrityViolationException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur localisé et le statut HTTP
	 *         CONFLICT.
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConflict(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		String extractedCode = extractErrorCode(
				Optional.ofNullable(ex.getRootCause()).map(Throwable::getMessage).orElse("default.error"));
		String msg = messageSource.getMessage(extractedCode, null, "Erreur d'intégrité des données",
				LocaleContextHolder.getLocale());
		return buildResponse(HttpStatus.CONFLICT, extractedCode, List.of(new ErrorDetail(null, msg)), request);
	}

	/**
	 * Utilise une expression régulière pour extraire le nom de la contrainte ou le code d'erreur du message de la cause
	 * racine.
	 *
	 * @param rootMessage
	 *            Le message de la cause racine à analyser.
	 * @return Le code d'erreur extrait, ou "default.error" si aucun code n'est trouvé.
	 */
	private String extractErrorCode(String rootMessage) {
		Pattern pattern = Pattern.compile("\\[(.*?)\\]\\s*$");
		Matcher matcher = pattern.matcher(rootMessage.toLowerCase());
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "default.error";
	}

	/**
	 * Vérifie si la cause racine est une erreur d'analyse JSON et retourne un message d'erreur approprié.
	 *
	 * @param ex
	 *            L'exception HttpMessageNotReadableException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec le message d'erreur et le statut HTTP
	 *         BAD_REQUEST.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		Throwable cause = ex.getCause();
		String msg;
		if (cause instanceof JsonParseException jp) {
			msg = "Syntaxe JSON invalide : " + jp.getOriginalMessage();
		} else if (cause instanceof InvalidTypeIdException iti) {
			msg = iti.getOriginalMessage().contains("missing type id property 'type'")
					? "Le champ discriminant 'type' est obligatoire."
					: "Syntaxe JSON invalide : " + iti.getOriginalMessage();
		} else {
			msg = "Message HTTP illisible.";
		}
		return buildResponse(HttpStatus.BAD_REQUEST, "message.not_readable", List.of(new ErrorDetail(null, msg)),
				request);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		String allowed = ex.getSupportedHttpMethods() != null
				? ex.getSupportedHttpMethods().stream().map(HttpMethod::name).collect(Collectors.joining(", "))
				: "";
		String msg = "La méthode " + ex.getMethod() + " n'est pas autorisée. Méthodes autorisées : " + allowed;
		return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "method.not_allowed", List.of(new ErrorDetail(null, msg)),
				request);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP CONFLICT lorsqu'une ressource a été modifiée par un autre
	 * utilisateur.
	 *
	 * @param ex
	 *            L'exception StaleObjectStateException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message de conflit et le statut HTTP
	 *         CONFLICT.
	 */
	@ExceptionHandler(StaleObjectStateException.class)
	public ResponseEntity<ApiErrorResponse> handleStale(StaleObjectStateException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.CONFLICT, "stale.object", List
				.of(new ErrorDetail(null, "La ressource a été modifiée par un autre utilisateur. Veuillez réessayer.")),
				request);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP NOT_FOUND lorsqu'aucun endpoint ne correspond à l'URL demandée.
	 *
	 * @param ex
	 *            L'exception NoHandlerFoundException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le statut HTTP NOT_FOUND.
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex,
			HttpServletRequest request) {
		String msg = "Aucun endpoint ne correspond à l'URL " + ex.getRequestURL();
		return buildResponse(HttpStatus.NOT_FOUND, "no_handler_found", List.of(new ErrorDetail(null, msg)), request);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP BAD_REQUEST lorsqu'un paramètre de chemin est manquant.
	 *
	 * @param request
	 *            La requête HTTP contenant les détails de la requête.
	 * @param ex
	 *            L'exception MissingPathVariableException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le statut HTTP
	 *         BAD_REQUEST.
	 */
	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<ApiErrorResponse> handleMissingPathVariable(HttpServletRequest request,
			MissingPathVariableException ex) {
		String msg = "Le paramètre de chemin manquant : " + ex.getVariableName();
		return buildResponse(HttpStatus.BAD_REQUEST, "missing.path_variable", List.of(new ErrorDetail(null, msg)),
				request);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP BAD_REQUEST lorsqu'un paramètre de requête est manquant.
	 *
	 * @param request
	 *            La requête HTTP contenant les détails de la requête.
	 * @param ex
	 *            L'exception MissingServletRequestParameterException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le statut HTTP
	 *         BAD_REQUEST.
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiErrorResponse> handleMissingServletParam(HttpServletRequest request,
			MissingServletRequestParameterException ex) {
		String msg = "Le paramètre de requête manquant : " + ex.getParameterName();
		return buildResponse(HttpStatus.BAD_REQUEST, "missing.request_param", List.of(new ErrorDetail(null, msg)),
				request);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP UNAUTHORIZED lorsqu'une exception AuthenticationException est
	 * levée.
	 *
	 * @param ex
	 *            L'exception AuthenticationException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le statut HTTP
	 *         UNAUTHORIZED.
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.UNAUTHORIZED, "access.unauthorized",
				List.of(new ErrorDetail(null, "Échec de l'authentification : " + ex.getMessage())), request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.FORBIDDEN, "access.denied",
				List.of(new ErrorDetail(null, "Accès refusé : vous n’avez pas les droits suffisants.")), request);
	}

	/**
	 * Journalise l'exception non gérée et retourne une réponse d'erreur avec le statut HTTP INTERNAL_SERVER_ERROR.
	 *
	 * @param ex
	 *            L'exception générique levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur générique et le statut HTTP
	 *         INTERNAL_SERVER_ERROR.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
		log.error("Erreur interne non gérée", ex);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "internal_error", List.of(new ErrorDetail(null,
				"Une erreur interne s'est produite. Contactez le support si le problème persiste.")), request);
	}
}
