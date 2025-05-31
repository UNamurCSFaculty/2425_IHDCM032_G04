package be.labil.anacarde.application.exception;

import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an operation is not allowed due to business rules. Typically results in an
 * HTTP 409 Conflict or 400 Bad Request.
 */
public class OperationNotAllowedException extends ApiErrorException {

	/**
	 * Constructs an OperationNotAllowedException with a single error detail.
	 *
	 * @param message
	 *            The detail message.
	 */
	public OperationNotAllowedException(String message) {
		super(HttpStatus.CONFLICT, ApiErrorCode.CONFLICT.code(), null, message);
	}

	/**
	 * Constructs an OperationNotAllowedException with a specific field, code, and message.
	 *
	 * @param field
	 *            The field related to the error.
	 * @param code
	 *            The error code.
	 * @param message
	 *            The detail message.
	 */
	public OperationNotAllowedException(String field, String code, String message) {
		super(HttpStatus.CONFLICT, code, field, message);
	}

	/**
	 * Constructs an OperationNotAllowedException with a list of error details.
	 *
	 * @param code
	 *            The general error code.
	 * @param errors
	 *            List of specific error details.
	 */
	public OperationNotAllowedException(String code, List<ErrorDetail> errors) {
		super(HttpStatus.CONFLICT, code, errors);
	}

	/**
	 * Constructs an OperationNotAllowedException with a specific HTTP status, code, and message.
	 *
	 * @param status
	 *            The HTTP status.
	 * @param code
	 *            The error code.
	 * @param message
	 *            The detail message.
	 */
	public OperationNotAllowedException(HttpStatus status, String code, String message) {
		super(status, code, null, message);
	}
}
