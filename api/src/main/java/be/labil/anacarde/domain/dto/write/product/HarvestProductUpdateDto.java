package be.labil.anacarde.domain.dto.write.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité HarvestProduct.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les produits récoltés.")
public class HarvestProductUpdateDto extends ProductUpdateDto {

	/** Producteur associé au produit récolté. */
	@NotNull(message = "Le producteur est requis")
	@Schema(description = "Producteur associé au produit récolté", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer producerId;

	/** Champ associé au produit récolté. */
	@NotNull(message = "Le champ est requis")
	@Schema(description = "Champ associé au produit récolté", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer fieldId;
}
