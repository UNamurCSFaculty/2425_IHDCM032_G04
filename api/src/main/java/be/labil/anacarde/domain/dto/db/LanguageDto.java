package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO pour l'entité Language.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour une langue.")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDto extends BaseDto {

	/** Nom de la langue. */
	@Schema(description = "Code de la langue", example = "fr", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le code de la langue est requis")
	private String code;

	/** Nom de la langue. */
	@Schema(description = "Nom de la langue", example = "Français", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom de la langue est requis")
	private String name;
}
