package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité AuctionStrategy.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les stratégies d'enchère.")
public class AuctionStrategyDto extends BaseDto {

	@NotBlank(message = "Le nom de la stratégie est requis")
	@Schema(description = "Nom de la stratégie d'enchère", example = "Stratégie Standard", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
