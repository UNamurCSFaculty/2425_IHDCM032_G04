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
 * Cette classe représente la structure d'une réponse d'erreur dans l'API. Elle encapsule un message
 * décrivant l'erreur survenue.
 */
public class ErrorResponse {
    @Schema(description = "Message d'erreur", example = "Sujet non trouvé")
    private String error;
}
