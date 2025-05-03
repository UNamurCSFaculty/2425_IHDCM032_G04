package be.labil.anacarde.application.exception;

public class EntityNotFoundException extends RuntimeException {
	public EntityNotFoundException(String message) {
		super(message);
	}
}
