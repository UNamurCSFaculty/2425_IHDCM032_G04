package be.labil.anacarde.application.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response structure")
/**
 * @brief Represents an error response.
 *
 * This class encapsulates an error message used in API responses when an error occurs.
 */
public class ErrorResponse {
    @Schema(description = "Error message", example = "Subject not found")
    private String error;
}
