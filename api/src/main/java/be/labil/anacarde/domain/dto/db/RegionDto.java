package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Region.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour une région.")
public class RegionDto extends BaseDto {

	/** Nom de la région. */
	@NotBlank(message = "Le nom de la région est requis")
	@Schema(description = "Nom de la région", example = "Région Nord", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
