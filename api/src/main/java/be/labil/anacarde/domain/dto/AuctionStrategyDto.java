package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité AuctionStrategy.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les stratégies d'enchère.")
public class AuctionStrategyDto {

	/** Identifiant unique de la stratégie d'enchère. */
	@Schema(description = "Identifiant unique de la stratégie d'enchère", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Nom de la stratégie d'enchère. */
	@NotBlank(message = "Le nom de la stratégie est requis")
	@Schema(description = "Nom de la stratégie d'enchère", example = "Stratégie Standard", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
