package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO pour l'entité Quality.
 */
@Data
@Schema(description = "Représente une qualité de produit.")
public class QualityDto {

	/** Identifiant unique de la qualité. */
	@Schema(description = "Identifiant de la qualité", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;

	/** Nom de la qualité. */
	@NotNull
	@NotBlank
	@Schema(description = "Nom de la qualité", example = "Extra", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
