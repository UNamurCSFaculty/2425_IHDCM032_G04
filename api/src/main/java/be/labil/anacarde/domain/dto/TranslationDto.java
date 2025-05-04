package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour l'entité Translation.
 */
@Data
@Schema(description = "Traduction multilingue identifiée par une clé unique.")
public class TranslationDto {

	/** Identifiant unique de la traduction. */
	@Schema(description = "Identifiant de la traduction", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;

	/** Clé unique identifiant la ressource à traduire. */
	@Schema(description = "Clé de la traduction", example = "home.title", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "La clé de traduction est requise")
	private String key;
}
