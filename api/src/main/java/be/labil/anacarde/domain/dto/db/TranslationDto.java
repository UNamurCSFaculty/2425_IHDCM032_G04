package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité Translation.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Traduction multilingue identifiée par une clé unique.")
public class TranslationDto extends BaseDto {

	/** Clé unique identifiant la ressource à traduire. */
	@Schema(description = "Clé de la traduction", example = "home.title", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "La clé de traduction est requise")
	private String key;
}
