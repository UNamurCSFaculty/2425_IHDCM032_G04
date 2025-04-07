package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

/**
 * DTO pour l'entité Auction.
 */
@Data
@Schema(description = "Objet de transfert de données pour les enchères.")
public class AuctionDto {

	/** Identifiant unique de l'enchère. */
	@Schema(description = "Identifiant unique de l'enchère", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Prix de l'enchère. */
	@NotNull(message = "Le prix est requis")
	@Schema(description = "Prix de l'enchère", example = "100.50", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal price;

	/** Quantité de produit associée à l'enchère. */
	@NotNull(message = "La quantité de produit est requise")
	@Schema(description = "Quantité de produit associée à l'enchère", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer productQuantity;

	/** Date d'expiration de l'enchère. */
	@NotNull(message = "La date d'expiration est requise")
	@Schema(description = "Date d'expiration de l'enchère", example = "2025-12-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime expirationDate;

	/** Date de création de l'enchère. */
	@NotNull(message = "La date de création est requise")
	@Schema(description = "Date de création de l'enchère", example = "2025-01-01T00:00:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime creationDate;

	/** Statut actif de l'enchère. */
	@NotNull(message = "Le statut actif est requis")
	@Schema(description = "Statut actif de l'enchère", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	private String active;

	/** Stratégie d'enchère associée. */
	@NotNull(message = "La stratégie est requise")
	@Schema(description = "Stratégie d'enchère associée", requiredMode = Schema.RequiredMode.REQUIRED)
	private AuctionStrategyDto strategy;

	/** Produit associé à l'enchère. */
	@NotNull(message = "Le produit est requis")
	@Schema(description = "Produit associé à l'enchère", requiredMode = Schema.RequiredMode.REQUIRED)
	private ProductDto product;

	/** Valeurs d'option associées à l'enchère. */
	@Schema(description = "Valeurs d'option associées à l'enchère")
	private Set<AuctionOptionValueDto> auctionOptionValues;
}
