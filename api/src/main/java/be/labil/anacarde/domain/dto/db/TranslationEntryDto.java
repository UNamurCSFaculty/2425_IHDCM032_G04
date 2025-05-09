package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité TranslationEntry.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Entrée d'une traduction pour une langue spécifique.")
public class TranslationEntryDto extends BaseDto {

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
