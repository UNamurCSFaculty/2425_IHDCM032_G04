package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

/**
 * DTO pour l'entité Action.
 */
@Data
@Schema(description = "Objet de transfert de données pour les enchères.")
public class AuctionDto {

	@Schema(description = "Identifiant unique de l'enchère", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@NotNull(message = "Le prix est requis")
	@Schema(description = "Prix de l'enchère", example = "100.50", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal price;

	@NotNull(message = "La quantité de produit est requise")
	@Schema(description = "Quantité de produit associée à l'enchère", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer productQuantity;

	@NotNull(message = "La date d'expiration est requise")
	@Schema(description = "Date d'expiration de l'enchère", example = "2025-12-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime expirationDate;

	@NotNull(message = "La date de création est requise")
	@Schema(description = "Date de création de l'enchère", example = "2025-01-01T00:00:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime creationDate;

	@NotNull(message = "Le statut actif est requis")
	@Schema(description = "Statut actif de l'enchère", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	private Boolean active;

	@NotNull(message = "La stratégie est requise")
	@Schema(description = "Stratégie d'enchère associée", requiredMode = Schema.RequiredMode.REQUIRED)
	private AuctionStrategyDto strategy;

	@NotNull(message = "Le produit est requis")
	@Schema(description = "Produit associé à l'enchère", requiredMode = Schema.RequiredMode.REQUIRED)
	private ProductDto product;

	@Schema(description = "Valeurs d'option associées à l'enchère")
	private Set<AuctionOptionValueDto> auctionOptionValues;
}
