package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Carrier.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les transporteurs.")
public class CarrierDto extends UserDto {

	/** Prix par kilomètre facturé par le transporteur. */
	@Schema(description = "Prix par kilomètre facturé par le transporteur", example = "1.50", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "Le prix par kilomètre est requis")
	private BigDecimal pricePerKm;

	/** Liste des identifiants des régions desservies par le transporteur. */
	@Schema(description = "Liste des identifiants des régions desservies par le transporteur", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
	private Set<Integer> regionIds;
}
