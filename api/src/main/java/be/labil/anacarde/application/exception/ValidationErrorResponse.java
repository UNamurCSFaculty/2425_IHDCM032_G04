package be.labil.anacarde.application.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validation error response structure")
/**
 * @brief Represents a response containing validation errors.
 *     <p>This class encapsulates a map of validation errors, where each key is a field name and the
 *     value is the error message associated with that field.
 */
public class ValidationErrorResponse {
    @Schema(
            description = "Map of validation errors (field -> message)",
            example = "{\"name\": \"The name must not be empty\"}")
    private Map<String, String> errors;
}
