package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de données pour les transformateurs.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "Objet de transfert de données pour les transformateurs.")
public class TransformerDto {

	/** Identifiant unique de l'acheteur. */
	@Schema(description = "Identifiant unique du Transformateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;
	/**
	 * Type de produit transformé (ex: AMANDE).
	 */
	@Schema(description = "Type de produit transformé", example = "amande", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le type de produit est requis")
	private String productType;

	/**
	 * Id du vendeur
	 */
	@Schema(description = "Identifiant du vendeur", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'identifiant du vendeur est requis")
	private Integer sellerId;

	/**
	 * Id de l'acheteur
	 */
	@Schema(description = "Identifiant de l'acheteur", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'identifiant de l'acheteur est requis")
	private Integer buyerId;
}