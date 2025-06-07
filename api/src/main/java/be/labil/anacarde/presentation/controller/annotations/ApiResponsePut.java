package be.labil.anacarde.presentation.controller.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour documenter les réponses standardisées des opérations PUT.
 * <p>
 * À appliquer sur les méthodes de contrôleur pour spécifier les codes de réponse dans la
 * documentation OpenAPI :
 * <ul>
 * <li><strong>200 Updated successfully</strong> – Ressource mise à jour avec succès, réponse
 * JSON.</li>
 * <li><strong>400 Bad Request</strong> – Requête invalide, renvoie un {@link ApiError}.</li>
 * <li><strong>401 Unauthorized</strong> – Requête non authentifiée, renvoie un
 * {@link ApiError}.</li>
 * <li><strong>403 Forbidden</strong> – Accès refusé, renvoie un {@link ApiError}.</li>
 * <li><strong>404 Not Found</strong> – Ressource introuvable, renvoie un {@link ApiError}.</li>
 * </ul>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Updated successfully", content = @Content(mediaType = "application/json")),
		@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ApiError.class))),
		@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiError.class))),
		@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ApiError.class))),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApiError.class)))})
public @interface ApiResponsePut {
}
