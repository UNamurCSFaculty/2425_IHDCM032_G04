package be.labil.anacarde.application.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente une réponse d’erreur au format standardisé renvoyée par l’API.
 * <p>
 * Contient les informations générales sur l’erreur (statut HTTP, horodatage,
 * chemin de la requête, code global) ainsi que la liste détaillée des erreurs
 * individuelles.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'erreur standardisée")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

	@Schema(description = "Code HTTP de la réponse", example = "400", required = true)
	private int status;

	@Schema(description = "Horodatage de l'erreur", example = "2025-05-03T14:22:05", required = true)
	private LocalDateTime timestamp;

	@Schema(description = "Chemin de la requête ayant provoqué l'erreur", example = "/api/users/123", required = true)
	private String path;

	@Schema(description = "Code global d'erreur", example = "validation.error", required = true)
	private String code;

	@Schema(description = "Liste des erreurs détaillées", required = true)
	private List<ErrorDetail> errors;
}