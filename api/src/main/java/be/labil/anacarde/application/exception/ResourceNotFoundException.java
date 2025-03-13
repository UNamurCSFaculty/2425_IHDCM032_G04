package be.labil.anacarde.application.exception;

/** Exception levée lorsqu'une ressource demandée est introuvable. */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construit une nouvelle ResourceNotFoundException avec le message d'erreur spécifié.
     *
     * @param message Le message détaillé expliquant la raison de l'exception.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
