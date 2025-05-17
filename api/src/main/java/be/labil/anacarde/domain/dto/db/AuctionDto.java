package be.labil.anacarde.domain.dto.db;

import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.db.user.UserMiniDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité Action.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les enchères.")
public class AuctionDto extends BaseDto {

	@NotNull(message = "Le prix est requis")
	@Schema(description = "Prix de l'enchère", example = "100.50", requiredMode = Schema.RequiredMode.REQUIRED)
	private Double price;

	@NotNull(message = "La quantité de produit est requise")
	@Schema(description = "Quantité de produit associée à l'enchère", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer productQuantity;

	@NotNull(message = "La date d'expiration est requise")
	@Schema(description = "Date d'expiration de l'enchère", example = "2025-12-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime expirationDate;

	@Schema(description = "Date de création de l'enchère", example = "2025-01-01T00:00:00", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime creationDate;

	@NotNull(message = "Le statut actif est requis")
	@Schema(description = "Statut actif de l'enchère", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	private Boolean active;

	@NotNull(message = "Le produit est requis")
	@Schema(description = "Produit associé à l'enchère", requiredMode = Schema.RequiredMode.REQUIRED)
	private ProductDto product;

	@NotNull(message = "Le trader est requis")
	@Schema(description = "Trader ayant créé l'enchère", requiredMode = Schema.RequiredMode.REQUIRED)
	private UserMiniDto trader;

	@NotNull(message = "Le statut de l'enchère est requis")
	@Schema(description = "Statut de l'enchère", requiredMode = Schema.RequiredMode.REQUIRED)
	private TradeStatusDto status;

	@Schema(description = "Liste des offres posées sur l'enchère", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<BidDto> bids;

	@Schema(description = "Options spécifiques à l'enchère")
	private AuctionOptionsDto options;
}
