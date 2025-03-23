package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les producteurs.")
public class ProducerDto {

	@Schema(description = "Identifiant unique du producteur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(description = "Identifiant de la coopérative associée", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Integer cooperativeId;

	@Schema(description = "Identifiant de l'acheteur associé", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer bidderId;
}
