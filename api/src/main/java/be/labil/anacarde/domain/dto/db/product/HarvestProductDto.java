package be.labil.anacarde.domain.dto.db.product;

import be.labil.anacarde.domain.dto.db.FieldDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
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
public class HarvestProductDto extends ProductDto {

	/** Producteur associé au produit récolté. */
	@NotNull(message = "Le producteur est requis")
	@Schema(description = "Producteur associé au produit récolté", requiredMode = Schema.RequiredMode.REQUIRED)
	private ProducerDetailDto producer;

	/** Champ associé au produit récolté. */
	@NotNull(message = "Le champ est requis")
	@Schema(description = "Champ associé au produit récolté", requiredMode = Schema.RequiredMode.REQUIRED)
	private FieldDto field;

	/** Produit transformé issu du produit brut. */
	@Schema(description = "Produits transformé correspondant")
	private TransformedProductDto transformedProduct;
}
