package be.labil.anacarde.application.exception;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * Récupère toutes les erreurs de validation contenues dans l'exception et les retourne sous forme d'un objet
	 * ValidationErrorResponse.
	 *
	 * @param ex
	 *            L'exception MethodArgumentNotValidException contenant les erreurs de validation.
	 * @return Une ResponseEntity contenant un objet ValidationErrorResponse détaillant les erreurs et le statut HTTP
	 *         BAD_REQUEST.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<>(new ValidationErrorResponse(errors), HttpStatus.BAD_REQUEST);
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
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
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
	public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
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
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		String defaultMessage = "Erreur d'intégrité des données.";
		Throwable rootCause = ex.getRootCause();
		String rootMessage = (rootCause != null) ? rootCause.getMessage() : "";

		String errorCode = extractErrorCode(rootMessage);

		// Réalise une recherche du message localisé en fonction du code d'erreur extrait
		String message = messageSource.getMessage(errorCode, null, defaultMessage, LocaleContextHolder.getLocale());

		return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.CONFLICT);
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
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		String message;
		Optional<Throwable> root = Optional.ofNullable(ex.getCause());
		if (root.isPresent() && root.get() instanceof JsonParseException) {
			message = "Erreur de syntaxe JSON : " + root.get().getMessage();
		} else {
			message = "Message HTTP illisible.";
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
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
	public ResponseEntity<ErrorResponse> handleStaleObjectStateException(StaleObjectStateException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse("La ressource a été modifiée par un autre utilisateur. Veuillez réessayer."));
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
	public ResponseEntity<ErrorResponse> handleGenericExceptions(Exception ex) {

		log.error("Unhandled internal error", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
				"Une erreur interne s'est produite. Veuillez contacter le support si le problème persiste."));
	}

	/**
	 * Retourne une réponse d'erreur avec le statut HTTP NOT_FOUND lorsqu'aucun endpoint ne correspond à l'URL demandée.
	 *
	 * @param ex
	 *            L'exception NoHandlerFoundException levée.
	 * @return Une ResponseEntity contenant un objet ErrorResponse avec un message d'erreur et le statut HTTP NOT_FOUND.
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
		String message = "Aucun endpoint ne correspond à l'URL " + ex.getRequestURL();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(message));
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
	public ResponseEntity<ErrorResponse> handleMissingPathVariableException(HttpServletRequest request,
			MissingPathVariableException ex) {
		String message = "Le paramètre de chemin manquant : " + ex.getVariableName();
		ErrorResponse errorResponse = new ErrorResponse(message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
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
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(HttpServletRequest request,
			MissingServletRequestParameterException ex) {
		String message = "Le paramètre de requête manquant : " + ex.getParameterName();
		ErrorResponse errorResponse = new ErrorResponse(message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
}
