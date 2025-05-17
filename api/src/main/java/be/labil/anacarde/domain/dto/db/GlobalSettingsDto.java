package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "Réglages globaux du système d'enchères")
public class GlobalSettingsDto {

	@Schema(description = "Stratégie par défaut")
	private AuctionStrategyDto defaultStrategy;

	@Schema(description = "Prix fixe au kg par défaut", example = "5.00")
	private BigDecimal defaultFixedPriceKg;

	@Schema(description = "Prix max au kg par défaut", example = "10.00")
	private BigDecimal defaultMaxPriceKg;

	@Schema(description = "Prix min au kg par défaut", example = "2.00")
	private BigDecimal defaultMinPriceKg;

	@Schema(description = "Filtrer automatiquement les enchères terminées", example = "true")
	private Boolean showOnlyActive;
}