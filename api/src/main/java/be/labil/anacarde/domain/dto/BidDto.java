package be.labil.anacarde.domain.dto;

import be.labil.anacarde.domain.dto.user.TraderDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Bid.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les offres d'achat (Bid).")
public class BidDto {

	/** Identifiant unique de l'offre d'achat. */
	@Schema(description = "Identifiant unique de l'offre d'achat", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Montant de l'offre. */
	@NotNull(message = "Le montant est requis")
	@Schema(description = "Montant de l'offre", example = "150.75", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal amount;

	/** Date de l'enchère. */
	@NotNull(message = "La date de l'enchère est requise")
	@Schema(description = "Date de l'enchère", example = "2025-04-07T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime auctionDate;

	/** Date de création de l'offre. */
	@Schema(description = "Date de création de l'offre", example = "2025-04-07T11:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime creationDate;

	/** Enchère associée à l'offre. */
	@NotNull(message = "L'enchère associée est requise")
	@Schema(description = "Enchère associée à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private AuctionDto auction;

	/** Trader ayant passé l'offre. */
	@NotNull(message = "Le trader est requis")
	@Schema(description = "Trader ayant passé l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private TraderDetailDto trader;

	/** Statut de l'offre. */
	@NotNull(message = "Le statut est requis")
	@Schema(description = "Statut de l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private BidStatusDto status;
}
