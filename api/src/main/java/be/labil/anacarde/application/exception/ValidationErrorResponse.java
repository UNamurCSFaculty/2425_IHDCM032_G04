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
@Schema(description = "Structure de réponse pour les erreurs de validation")
/**
 * Cette classe encapsule une carte contenant les erreurs de validation. Chaque clé représente le
 * nom d'un champ et la valeur correspondante est le message d'erreur associé.
 */
public class ValidationErrorResponse {
    @Schema(
            description = "Map des erreurs de validation (champ -> message)",
            example = "{\"name\": \"Le nom ne doit pas être vide\"}")
    private Map<String, String> errors;
}
