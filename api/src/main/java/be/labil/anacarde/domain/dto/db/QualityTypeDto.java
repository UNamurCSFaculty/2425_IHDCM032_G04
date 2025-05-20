package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité QualityType.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Représente un type de qualité de produit.")
public class QualityTypeDto extends BaseDto {

	/** Nom du type de qualité. */
	@NotNull
	@NotBlank
	@Schema(description = "Nom du type de qualité", example = "Anacarde", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}