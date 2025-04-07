package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class TransformedProductDto extends ProductDto {

	/** Identifiant du produit transformé. */
	@Schema(description = "Identifiant du produit transformé", example = "TP001", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "L'identifiant est requis")
	private String identifier;

	/** Emplacement du produit transformé. */
	@Schema(description = "Emplacement du produit transformé", example = "Zone A", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "La localisation est requise")
	private String location;

	/** Transformateur associé au produit transformé. */
	@Schema(description = "Transformateur associé", requiredMode = Schema.RequiredMode.REQUIRED)
	private TransformerDetailDto transformer;
}
