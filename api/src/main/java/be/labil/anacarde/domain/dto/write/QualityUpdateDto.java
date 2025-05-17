package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Écriture d'une qualité")
public class QualityUpdateDto {

	@NotNull
	@NotBlank
	@Schema(description = "Nom de la qualité", example = "Extra", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	@NotNull
	@Schema(description = "Identifiant du type de qualité", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer qualityTypeId;
}
