package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Objet de transfert de données pour les entités QualityDocument.")
public class QualityDocumentDto extends DocumentDto {

	/** Identifiant de la certification qualité associée. */
	@Schema(description = "Identifiant de la certification de qualité associée", example = "3")
	@NotNull(message = "Identifiant de la certification de qualité associée")
	private Integer qualityCertificationId;

}
