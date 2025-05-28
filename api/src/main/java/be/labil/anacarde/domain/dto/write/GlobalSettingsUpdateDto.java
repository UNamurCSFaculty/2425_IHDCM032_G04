package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "Mise à jour des réglages globaux")
public class GlobalSettingsUpdateDto {

	@Schema(description = "ID de la stratégie par défaut", example = "1")
	private Integer defaultStrategyId;

	@Schema(description = "Prix fixe au kg par défaut", example = "5.00")
	private BigDecimal defaultFixedPriceKg;

	@Schema(description = "Prix max au kg par défaut", example = "10.00")
	private BigDecimal defaultMaxPriceKg;

	@Schema(description = "Prix min au kg par défaut", example = "2.00")
	private BigDecimal defaultMinPriceKg;

	@NotNull(message = "Le flag showOnlyActive est requis")
	@Schema(description = "Filtrer les enchères terminées", example = "true", required = true)
	private Boolean showOnlyActive;

	@NotNull(message = "Le flag forceBetterBids est requis")
	@Schema(description = "Forcer de faire des sur enchères meilleures que la précédente", example = "true", required = true)
	private Boolean forceBetterBids = false;

	@Schema(description = "Incrément minimum d'une sur enchère")
	private Integer minIncrement = 1;
}
