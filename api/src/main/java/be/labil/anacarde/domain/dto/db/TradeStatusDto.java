package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité TradeStatus.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour le statut d'une offre d'achat.")
public class TradeStatusDto extends BaseDto {

	/** Nom du statut. */
	@NotBlank(message = "Le nom du statut est requis")
	@Schema(description = "Nom du statut", example = "Accepté", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
