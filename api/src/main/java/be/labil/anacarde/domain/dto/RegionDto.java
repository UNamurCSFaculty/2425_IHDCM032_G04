package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Region.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour une région.")
public class RegionDto {

	/** Identifiant unique de la région. */
	@Schema(description = "Identifiant unique de la région", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;

	/** Nom de la région. */
	@NotBlank(message = "Le nom de la région est requis")
	@Schema(description = "Nom de la région", example = "Région Nord", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
