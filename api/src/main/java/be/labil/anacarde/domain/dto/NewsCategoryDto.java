package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour l'entité NewsCategory.
 */
@Data
@Schema(description = "Objet de transfert de données pour les catégories de news.")
public class NewsCategoryDto {

	/** Identifiant unique de la catégorie. */
	@Schema(description = "Identifiant unique de la catégorie", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Nom de la catégorie. */
	@Schema(description = "Nom de la catégorie", example = "Sports", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom de la catégorie est requis")
	private String name;
}
