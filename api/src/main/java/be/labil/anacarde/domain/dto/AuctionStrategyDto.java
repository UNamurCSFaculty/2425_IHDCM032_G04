package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les stratégies d'enchère.")
public class AuctionStrategyDto {

	@Schema(description = "Identifiant unique de la stratégie d'enchère", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@NotBlank(message = "Le nom de la stratégie est requis")
	@Schema(description = "Nom de la stratégie d'enchère", example = "Stratégie Standard", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
