package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Options spécifiques à une enchère")
public class AuctionOptionsDto {

	@Schema(description = "Stratégie de l'enchère")
	private AuctionStrategyDto strategy;

	@Schema(description = "Prix fixe au kg", example = "7.50")
	private Double fixedPriceKg;

	@Schema(description = "Prix max au kg", example = "12.00")
	private Double maxPriceKg;

	@Schema(description = "Prix min au kg", example = "3.00")
	private Double minPriceKg;

	@Schema(description = "Prix d'achat immédiat", example = "150.00")
	private Double buyNowPrice;

	@Schema(description = "Afficher au public", example = "true")
	private Boolean showPublic;

	@Schema(description = "Forcer les meilleures enchères", example = "false")
	private Boolean forceBetterBids;

	@Schema(description = "Incrément minimum d'une sur enchère")
	private Integer minIncrement = 1;
}