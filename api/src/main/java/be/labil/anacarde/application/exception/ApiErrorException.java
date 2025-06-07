package be.labil.anacarde.application.exception;

import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Exception métier permettant de construire une réponse d’erreur standardisée
 * ({@link ApiErrorResponse}) avec un statut HTTP personnalisé, un code d’erreur
 * et une liste de détails d’erreur.
 * <p>
 * Cette exception est capturée par un handler global pour générer une réponse API
 * complète à l’utilisateur.
 */
public class ApiErrorException extends RuntimeException {

	private final HttpStatus status;
	private final String code;
	private final List<ErrorDetail> errors;

	/** Pour plusieur erreurs */
	public ApiErrorException(HttpStatus status, String code, List<ErrorDetail> errors) {
		super(errors != null && !errors.isEmpty() ? errors.getFirst().getMessage() : null);
		this.status = status;
		this.code = code;
		this.errors = errors;
	}

	/** Surcharge pour une seule erreur */
	public ApiErrorException(HttpStatus status, String code, String field, String message) {
		super(message);
		this.status = status;
		this.code = code;
		this.errors = List.of(new ErrorDetail(field, code, message));
	}

	public HttpStatus getStatus() {
		return status;
	}
	public String getCode() {
		return code;
	}
	public List<ErrorDetail> getErrors() {
		return errors;
	}
}