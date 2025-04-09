package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO abstrait pour l'entité Product.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour un produit.")
public abstract class ProductDto {

	/** Identifiant unique du produit. */
	@Schema(description = "Identifiant unique du produit", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;
}
