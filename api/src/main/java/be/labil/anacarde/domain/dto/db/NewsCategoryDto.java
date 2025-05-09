package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité NewsCategory.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les catégories de news.")
public class NewsCategoryDto extends BaseDto {

	/** Nom de la catégorie. */
	@Schema(description = "Nom de la catégorie", example = "Sports", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom de la catégorie est requis")
	private String name;
}
