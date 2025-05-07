package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Language.
 */
@Data
@Schema(description = "Objet de transfert de données pour une langue.")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDto {

	/** Identifiant unique de la langue. */
	private Integer id;

	/** Nom de la langue. */
	@Schema(description = "Code de la langue", example = "fr", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le code de la langue est requis")
	private String code;

	/** Nom de la langue. */
	@Schema(description = "Nom de la langue", example = "Français", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom de la langue est requis")
	private String name;
}
