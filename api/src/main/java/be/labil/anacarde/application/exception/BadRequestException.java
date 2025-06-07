package be.labil.anacarde.application.exception;

/**
 * Exception levée lorsqu'une requête reçue par l'application est invalide
 * (équivalent à un HTTP 400 Bad Request).
 */
public class BadRequestException extends RuntimeException {

	/**
	 * Crée une nouvelle {@code BadRequestException} avec un message d'erreur détaillé.
	 *
	 * @param message le message décrivant la raison de l’erreur de requête
	 */
	public BadRequestException(String message) {
		super(message);
	}
}
