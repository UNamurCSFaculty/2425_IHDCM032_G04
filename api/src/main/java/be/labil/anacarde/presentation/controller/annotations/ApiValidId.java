package be.labil.anacarde.presentation.controller.annotations;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.lang.annotation.*;

/**
 * Annotation combinée pour valider les paramètres d’identifiant de ressource dans les contrôleurs.
 * <p>
 * Appliquée sur un paramètre, elle garantit que :
 * <ul>
 * <li>La valeur est non nulle ({@link NotNull}).</li>
 * <li>La valeur est un entier strictement positif ({@link Positive}).</li>
 * <li>Swagger/OpenAPI documente le paramètre comme un identifiant requis, avec la description et
 * l’exemple fournis ({@link Parameter}).</li>
 * </ul>
 * <p>
 * À utiliser pour tous les paramètres de type ID afin d’uniformiser la validation et la
 * documentation.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Positive(message = "L'identifiant doit être un entier positif")
@NotNull(message = "L'identifiant est obligatoire")
@Parameter(description = "Identifiant de la ressource", example = "1", required = true)
public @interface ApiValidId {
}