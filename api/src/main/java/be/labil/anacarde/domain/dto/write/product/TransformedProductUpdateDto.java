package be.labil.anacarde.domain.dto.write.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité TransformedProduct.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les produits transformés.")
public class TransformedProductUpdateDto extends ProductUpdateDto {

	/** Identifiant du produit transformé. */
	@Schema(description = "Identifiant du produit transformé", example = "TP001", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "L'identifiant est requis")
	private String identifier;

	/** Transformateur associé au produit transformé. */
	@Schema(description = "Transformateur associé", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer transformerId;

	/** Produits bruts dont est issu le produit transformé. */
	@Schema(description = "Produits bruts d'origine")
	private List<Integer> harvestProductIds = new ArrayList<>();
}
