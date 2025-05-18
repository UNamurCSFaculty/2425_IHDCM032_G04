package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité Quality.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Représente une qualité de produit.")
public class QualityDto extends BaseDto {

	/** Nom de la qualité. */
	@NotNull
	@NotBlank
	@Schema(description = "Nom de la qualité", example = "Extra", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	/** Identifiant du type de qualité. */
	@NotNull
	@Schema(description = "Identifiant du type de qualité", requiredMode = Schema.RequiredMode.REQUIRED)
	private QualityTypeDto qualityType;
}
