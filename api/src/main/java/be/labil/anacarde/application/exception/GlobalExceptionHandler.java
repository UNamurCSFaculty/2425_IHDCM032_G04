package be.labil.anacarde.application.exception;

import com.fasterxml.jackson.core.JsonParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
/**
 * @brief Global exception handler for the application.
 *     <p>This class handles exceptions thrown throughout the application and translates them into
 *     meaningful HTTP responses. It handles validation errors, resource not found exceptions, data
 *     integrity violations, HTTP message not readable exceptions, optimistic locking errors, and
 *     generic exceptions.
 */
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    /**
     * @brief Constructor for GlobalExceptionHandler.
     * @param messageSource The MessageSource used for retrieving localized messages.
     */
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * @brief Handles validation errors from method argument validation.
     *     <p>This method collects all validation errors from the exception and returns them in a
     *     ValidationErrorResponse.
     * @param ex The MethodArgumentNotValidException containing validation errors.
     * @return A ResponseEntity containing a ValidationErrorResponse with error details and HTTP
     *     status BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        error -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            errors.put(fieldName, errorMessage);
                        });
        return new ResponseEntity<>(new ValidationErrorResponse(errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * @brief Handles ResourceNotFoundException.
     *     <p>This method returns an error response with HTTP status NOT_FOUND when a requested
     *     resource is not found.
     * @param ex The ResourceNotFoundException that was thrown.
     * @return A ResponseEntity containing an ErrorResponse with the exception message and HTTP
     *     status NOT_FOUND.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * @brief Handles data integrity violations, such as unique constraint errors.
     *     <p>This method extracts an error code from the root cause of the exception, retrieves a
     *     localized message, and returns an error response with HTTP status CONFLICT.
     * @param ex The DataIntegrityViolationException that was thrown.
     * @return A ResponseEntity containing an ErrorResponse with a localized error message and HTTP
     *     status CONFLICT.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        String defaultMessage = "Data integrity error.";
        Throwable rootCause = ex.getRootCause();
        String rootMessage = (rootCause != null) ? rootCause.getMessage() : "";

        // Extract error code (e.g., constraint name) from the root message
        String errorCode = extractErrorCode(rootMessage);

        // Get localized message from messageSource; the message is now externalized
        String message =
                messageSource.getMessage(
                        errorCode, null, defaultMessage, LocaleContextHolder.getLocale());

        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.CONFLICT);
    }

    /**
     * @brief Extracts an error code from the provided root message.
     *     <p>This method uses a regular expression to extract the constraint name or error code
     *     from the root message.
     * @param rootMessage The root cause message from which to extract the error code.
     * @return The extracted error code, or "default.error" if not found.
     */
    private String extractErrorCode(String rootMessage) {
        Pattern pattern = Pattern.compile("constraint \\[(.*?)\\]");
        Matcher matcher = pattern.matcher(rootMessage.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "default.error";
    }

    /**
     * @brief Handles exceptions when the HTTP message is not readable.
     *     <p>This method checks if the root cause is a JSON parsing error and returns an
     *     appropriate error message.
     * @param ex The HttpMessageNotReadableException that was thrown.
     * @return A ResponseEntity containing an ErrorResponse with the error message and HTTP status
     *     BAD_REQUEST.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        String message;
        Optional<Throwable> root = Optional.ofNullable(ex.getCause());
        if (root.isPresent() && root.get() instanceof JsonParseException) {
            message = "JSON syntax error: " + root.get().getMessage();
        } else {
            message = "Unreadable HTTP message.";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
    }

    /**
     * @brief Handles optimistic locking errors (versioning conflicts).
     *     <p>This method returns an error response with HTTP status CONFLICT when a resource has
     *     been modified by another user.
     * @param ex The StaleObjectStateException that was thrown.
     * @return A ResponseEntity containing an ErrorResponse with a conflict message and HTTP status
     *     CONFLICT.
     */
    @ExceptionHandler(org.hibernate.StaleObjectStateException.class)
    public ResponseEntity<ErrorResponse> handleStaleObjectStateException(
            org.hibernate.StaleObjectStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                "The resource has been modified by another user. Please try again."));
    }

    /**
     * @brief Handles all other generic exceptions.
     *     <p>This method logs the unhandled exception and returns an error response with HTTP
     *     status INTERNAL_SERVER_ERROR.
     * @param ex The generic Exception that was thrown.
     * @return A ResponseEntity containing an ErrorResponse with a generic error message and HTTP
     *     status INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericExceptions(Exception ex) {
        log.error("Unhandled internal error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                "An internal error has occurred. Please contact support if the problem persists."));
    }
}
