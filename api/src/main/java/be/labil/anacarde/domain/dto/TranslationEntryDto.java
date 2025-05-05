package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO pour l'entité TranslationEntry.
 */
@Data
@Schema(description = "Entrée d'une traduction pour une langue spécifique.")
public class TranslationEntryDto {

	/** Identifiant unique de l'entrée de traduction. */
	@Schema(description = "Identifiant de l'entrée de traduction", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;

	/** Référence vers la traduction (généralement l'ensemble multilingue). */
	@Schema(description = "Identifiant de la traduction", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La référence vers la traduction est requise")
	private Integer translationId;

	/** Référence vers la langue. */
	@Schema(description = "Identifiant de la langue", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La langue est requise")
	private Integer languageId;

	/** Texte traduit dans la langue correspondante. */
	@Schema(description = "Texte traduit", example = "Bonjour", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le texte traduit est requis")
	private String text;
}
