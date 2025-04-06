package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de données pour les acheteurs (Buyer).
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les acheteurs.")
public class BuyerDto {

	/** Identifiant unique de l'acheteur. */
	@Schema(description = "Identifiant unique de l'acheteur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Identifiant de l'enchérisseur associé à l'acheteur. */
	@Schema(description = "Identifiant du bidder associé", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'indentifiant du bidder associé ne doit pas être vide")
	private Integer bidderId;

	/** Identifiant de l'utilisateur associé à l'acheteur. */
	@Schema(description = "Identifiant de l'utilisateur associé", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'identifiant de l'utilisateur associé ne doit pas être vide")
	private Integer userId;
}