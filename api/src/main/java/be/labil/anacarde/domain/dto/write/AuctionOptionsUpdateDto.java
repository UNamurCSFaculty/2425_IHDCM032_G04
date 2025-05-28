package be.labil.anacarde.domain.dto.write;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Mise à jour des options d'enchère")
public class AuctionOptionsUpdateDto {

	@Schema(description = "ID de la stratégie", example = "2")
	private Integer strategyId;

	@Schema(description = "Prix fixe au kg", example = "7.50")
	private double fixedPriceKg;

	@Schema(description = "Prix max au kg", example = "12.00")
	private double maxPriceKg;

	@Schema(description = "Prix min au kg", example = "3.00")
	private double minPriceKg;

	@Schema(description = "Prix d'achat immédiat", example = "150.00")
	private double buyNowPrice;

	@Schema(description = "Afficher au public", example = "true")
	private Boolean showPublic;

	@Schema(description = "Forcer les meilleures enchères", example = "false")
	private Boolean forceBetterBids = false;

	@Schema(description = "Incrément minimum d'une sur enchère")
	private Integer minIncrement = 1;
}
