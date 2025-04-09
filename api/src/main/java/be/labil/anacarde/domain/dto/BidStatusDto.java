package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité BidStatus.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour le statut d'une offre d'achat.")
public class BidStatusDto {

	/** Identifiant unique du statut. */
	@Schema(description = "Identifiant unique du statut", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Nom du statut. */
	@NotBlank(message = "Le nom du statut est requis")
	@Schema(description = "Nom du statut", example = "Accepté", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
