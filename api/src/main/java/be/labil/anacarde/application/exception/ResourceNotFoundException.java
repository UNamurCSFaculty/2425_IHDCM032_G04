package be.labil.anacarde.application.exception;

/** Exception thrown when a requested resource is not found. */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the exception.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
