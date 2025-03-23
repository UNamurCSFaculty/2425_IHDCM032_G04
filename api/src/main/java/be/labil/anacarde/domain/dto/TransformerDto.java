package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de données pour les transformateurs.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les transformateurs.")
public class TransformerDto extends BuyerDto {

	/** Type de produit transformé (ex: AMANDE). */
	@Schema(description = "Type de produit transformé", example = "amande", requiredMode = Schema.RequiredMode.REQUIRED)
	private String productType;
}