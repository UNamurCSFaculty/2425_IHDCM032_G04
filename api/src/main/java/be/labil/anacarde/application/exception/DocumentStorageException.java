package be.labil.anacarde.application.exception;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Exception spécifique aux erreurs de stockage de documents, encapsulant un code d'erreur
 * personnalisé et loguant l'exception d'origine.
 */
@Slf4j
public class DocumentStorageException extends ApiErrorException {

	/**
	 * Crée une nouvelle {@code DocumentStorageException}.
	 *
	 * Construit une erreur API de type {@link HttpStatus#INTERNAL_SERVER_ERROR} avec un code
	 * {@code STORAGE_ERROR} et détaille l'erreur. Enregistre également le message et la cause dans
	 * le log d'erreur.
	 *
	 * @param msg
	 *            le message décrivant le contexte de l’erreur de stockage
	 * @param cause
	 *            l’exception d’origine ayant provoqué l’échec du stockage
	 */
	public DocumentStorageException(String msg, Throwable cause) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.STORAGE_ERROR.code(),
				List.of(new ErrorDetail(null, ApiErrorCode.STORAGE_ERROR.code(), msg)));
		log.error(msg, cause);
	}
}