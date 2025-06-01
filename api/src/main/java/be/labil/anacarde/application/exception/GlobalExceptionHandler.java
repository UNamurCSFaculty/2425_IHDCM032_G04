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
import org.springframework.security.authentication.DisabledException;
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
 * Cette classe gère les exceptions levées dans l'application et les traduit en réponses HTTP
 * significatives. Elle prend en charge les erreurs de validation, les exceptions de ressource non
 * trouvée, les violations d'intégrité des données, les erreurs de lecture des messages HTTP, les
 * conflits d'accès concurrent, ainsi que les exceptions génériques.
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
	 * Construit une réponse d'erreur standardisée.
	 *
	 * @param status
	 *            Le code de statut HTTP.
	 * @param code
	 *            Le code d'erreur.
	 * @param detailMessage
	 *            Le message détaillé de l'erreur.
	 * @param request
	 *            La requête HTTP à partir de laquelle l'erreur a été générée.
	 * @return La réponse d'erreur standardisée.
	 */
	private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String code,
			String detailMessage, HttpServletRequest request) {

		ErrorDetail item = new ErrorDetail(null, code, detailMessage);
		return buildResponse(status, code, List.of(item), request);
	}

	/**
	 * Construit une réponse d'erreur standardisée avec une liste d'erreurs.
	 *
	 * @param status
	 *            Le code de statut HTTP.
	 * @param code
	 *            Le code d'erreur.
	 * @param errors
	 *            La liste des détails d'erreur.
	 * @param request
	 *            La requête HTTP à partir de laquelle l'erreur a été générée.
	 * @return La réponse d'erreur standardisée.
	 */
	private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String code,
			List<ErrorDetail> errors, HttpServletRequest request) {

		ApiErrorResponse body = new ApiErrorResponse(status.value(), LocalDateTime.now(),
				request.getRequestURI(), code, errors);
		return ResponseEntity.status(status).body(body);
	}

	/**
	 * Gère les exceptions ApiErrorException.
	 *
	 * @param ex
	 *            L'exception ApiErrorException à gérer.
	 * @param req
	 *            La requête HTTP à partir de laquelle l'erreur a été générée.
	 * @return La réponse d'erreur standardisée.
	 */
	@ExceptionHandler(ApiErrorException.class)
	public ResponseEntity<ApiErrorResponse> handleApiError(ApiErrorException ex,
			HttpServletRequest req) {
		return buildResponse(ex.getStatus(), ex.getCode(), ex.getErrors(), req);
	}

	/**
	 * Gère les exceptions OperationNotAllowedException.
	 *
	 * @param ex
	 *            L'exception OperationNotAllowedException à gérer.
	 * @param request
	 *            La requête HTTP à partir de laquelle l'erreur a été générée.
	 * @return La réponse d'erreur standardisée, typiquement avec un statut 409 Conflict.
	 */
	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<ApiErrorResponse> handleOperationNotAllowed(
			OperationNotAllowedException ex, HttpServletRequest request) {
		return buildResponse(ex.getStatus(), ex.getCode(), ex.getErrors(), request);
	}

	/*
	 * Gère les exceptions de validation des arguments de méthode.
	 *
	 * @param ex L'exception MethodArgumentNotValidException à gérer.
	 * 
	 * @param req La requête HTTP à partir de laquelle l'erreur a été générée.
	 * 
	 * @return La réponse d'erreur standardisée.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest req) {

		String code = ApiErrorCode.VALIDATION_ERROR.code();
		List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> new ErrorDetail(err.getField(), code, err.getDefaultMessage()))
				.collect(Collectors.toList());

		return buildResponse(HttpStatus.BAD_REQUEST, code, details, req);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP NOT_FOUND lorsqu'une ressource n'est pas
	 * trouvée.
	 *
	 * @param ex
	 *            L'exception ResourceNotFoundException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec le message de l'exception et
	 *         le statut HTTP NOT_FOUND.
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex,
			HttpServletRequest req) {

		return buildResponse(HttpStatus.NOT_FOUND, ApiErrorCode.RESOURCE_NOT_FOUND.code(),
				ex.getMessage(), req);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP BAD_REQUEST lorsqu'une exception
	 * BadRequestException est levée. Cela peut être dû à des paramètres manquants ou invalides dans
	 * la requête.
	 *
	 * @param ex
	 *            L'exception BadRequestException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec le message de l'exception et
	 *         le statut HTTP BAD_REQUEST.
	 */
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex,
			HttpServletRequest req) {

		return buildResponse(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
				ex.getMessage(), req);
	}

	/**
	 * Extrait un code d'erreur à partir de la cause racine de l'exception, récupère un message
	 * localisé et retourne une réponse d'erreur avec le statut HTTP CONFLICT.
	 *
	 * @param ex
	 *            L'exception DataIntegrityViolationException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur localisé
	 *         et le statut HTTP CONFLICT.
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConflict(DataIntegrityViolationException ex,
			HttpServletRequest req) {

		/**
		 * Utilise une expression régulière pour extraire le nom de la contrainte ou le code
		 * d'erreur du message de la cause racine.
		 *
		 * @param rootMessage
		 *            Le message de la cause racine à analyser.
		 * @return Le code d'erreur extrait, ou "default.error" si aucun code n'est trouvé.
		 */
		String code = Optional.ofNullable(ex.getRootCause()).map(Throwable::getMessage)
				.map(this::extractErrorCode).orElse(ApiErrorCode.DEFAULT_ERROR.code());

		String msg = messageSource.getMessage(code, null, "Erreur d'intégrité des données",
				LocaleContextHolder.getLocale());

		return buildResponse(HttpStatus.CONFLICT, code, msg, req);
	}

	/**
	 * Vérifie si la cause racine est une erreur d'analyse JSON et retourne un message d'erreur
	 * approprié.
	 *
	 * @param ex
	 *            L'exception HttpMessageNotReadableException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec le message d'erreur et le
	 *         statut HTTP BAD_REQUEST.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
			HttpServletRequest req) {

		Throwable cause = ex.getCause();
		String detail;
		if (cause instanceof JsonParseException jp) {
			detail = "Syntaxe JSON invalide : " + jp.getOriginalMessage();
		} else if (cause instanceof InvalidTypeIdException iti) {
			detail = iti.getOriginalMessage().contains("missing type id property 'type'")
					? "Le champ discriminant 'type' est obligatoire."
					: "Syntaxe JSON invalide : " + iti.getOriginalMessage();
		} else {
			detail = "Message HTTP illisible.";
		}

		return buildResponse(HttpStatus.BAD_REQUEST, ApiErrorCode.MESSAGE_NOT_READABLE.code(),
				detail, req);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP METHOD_NOT_ALLOWED lorsqu'une méthode HTTP
	 * non autorisée est utilisée.
	 *
	 * @param ex
	 *            L'exception HttpRequestMethodNotSupportedException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le
	 *         statut HTTP METHOD_NOT_ALLOWED.
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(
			HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {

		String allowed = ex.getSupportedHttpMethods() != null
				? ex.getSupportedHttpMethods().stream().map(HttpMethod::name)
						.collect(Collectors.joining(", "))
				: "";

		String msg = "La méthode " + ex.getMethod() + " n'est pas autorisée. Méthodes autorisées : "
				+ allowed;

		return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, ApiErrorCode.METHOD_NOT_ALLOWED.code(),
				msg, req);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP CONFLICT lorsqu'une ressource a été
	 * modifiée par un autre utilisateur.
	 *
	 * @param ex
	 *            L'exception StaleObjectStateException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message de conflit et le
	 *         statut HTTP CONFLICT.
	 */
	@ExceptionHandler(StaleObjectStateException.class)
	public ResponseEntity<ApiErrorResponse> handleStale(StaleObjectStateException ex,
			HttpServletRequest req) {

		return buildResponse(HttpStatus.CONFLICT, ApiErrorCode.STALE_OBJECT.code(),
				"La ressource a été modifiée par un autre utilisateur. Veuillez réessayer.", req);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP NOT_FOUND lorsqu'aucun endpoint ne
	 * correspond à l'URL demandée.
	 *
	 * @param ex
	 *            L'exception NoHandlerFoundException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le
	 *         statut HTTP NOT_FOUND.
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex,
			HttpServletRequest req) {

		String detail = "Aucun endpoint ne correspond à l'URL " + ex.getRequestURL();
		return buildResponse(HttpStatus.NOT_FOUND, ApiErrorCode.NO_HANDLER_FOUND.code(), detail,
				req);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP BAD_REQUEST lorsqu'un paramètre de chemin
	 * est manquant.
	 *
	 * @param ex
	 *            L'exception MissingPathVariableException levée.
	 * @param req
	 *            La requête HTTP contenant les détails de la requête.
	 * 
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le
	 *         statut HTTP BAD_REQUEST.
	 */
	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<ApiErrorResponse> handleMissingPathVariable(
			MissingPathVariableException ex, HttpServletRequest req) {

		String detail = "Le paramètre de chemin manquant : " + ex.getVariableName();
		return buildResponse(HttpStatus.BAD_REQUEST, ApiErrorCode.MISSING_PATH_VARIABLE.code(),
				detail, req);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP BAD_REQUEST lorsqu'un paramètre de requête
	 * est manquant.
	 *
	 * @param ex
	 *            L'exception MissingServletRequestParameterException levée.
	 * @param req
	 *            La requête HTTP contenant les détails de la requête.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le
	 *         statut HTTP BAD_REQUEST.
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiErrorResponse> handleMissingServletParam(
			MissingServletRequestParameterException ex, HttpServletRequest req) {

		String detail = "Le paramètre de requête manquant : " + ex.getParameterName();
		return buildResponse(HttpStatus.BAD_REQUEST, ApiErrorCode.MISSING_REQUEST_PARAM.code(),
				detail, req);
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP UNAUTHORIZED lorsqu'une exception
	 * AuthenticationException est levée.
	 *
	 * @param ex
	 *            L'exception AuthenticationException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le
	 *         statut HTTP UNAUTHORIZED.
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
			AuthenticationException ex, HttpServletRequest req) {

		if (ex instanceof DisabledException) {
			return buildResponse(HttpStatus.UNAUTHORIZED, ApiErrorCode.ACCESS_DISABLED.code(),
					"L'accès est désactivé pour cet utilisateur.", req);
		}

		return buildResponse(HttpStatus.UNAUTHORIZED, ApiErrorCode.ACCESS_UNAUTHORIZED.code(),
				"Échec de l'authentification : " + ex.getMessage(), req);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex,
			HttpServletRequest req) {

		return buildResponse(HttpStatus.FORBIDDEN, ApiErrorCode.ACCESS_DENIED.code(),
				"Accès refusé : vous n’avez pas les droits suffisants.", req);
	}

	/**
	 * Journalise l'exception non gérée et retourne une réponse d'erreur avec le statut HTTP
	 * INTERNAL_SERVER_ERROR.
	 *
	 * @param ex
	 *            L'exception générique levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur
	 *         générique et le statut HTTP INTERNAL_SERVER_ERROR.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {

		log.error("Erreur interne non gérée", ex);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_ERROR.code(),
				"Une erreur interne s'est produite. Contactez le support si le problème persiste.",
				req);
	}

	// extrait le code entre crochets [xxx]
	private String extractErrorCode(String rootMessage) {
		Matcher m = Pattern.compile("\\[(.*?)\\]\\s*$").matcher(rootMessage.toLowerCase());
		return m.find() ? m.group(1) : ApiErrorCode.DEFAULT_ERROR.code();
	}
}
