package be.labil.anacarde.domain.dto.db;

import be.labil.anacarde.domain.dto.db.user.UserMiniDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Bid.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les offres d'achat (Bid).")
public class BidDto extends BaseDto {

	/** Montant de l'offre. */
	@NotNull(message = "Le montant est requis")
	@Schema(description = "Montant de l'offre", example = "150.75", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal amount;

	/** Date de création de l'offre. */
	@Schema(description = "Date de création de l'offre", example = "2025-04-07T11:30:00", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime creationDate;

	/** Enchère associée à l'offre. */
	@NotNull(message = "L'identifiant de l'enchère associée est requise")
	@Schema(description = "Identifiant de l'enchère associée à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer auctionId;

	/** Trader ayant passé l'offre. */
	@NotNull(message = "Le trader est requis")
	@Schema(description = "Trader ayant passé l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private UserMiniDto trader;

	/** Statut de l'offre. */
	@NotNull(message = "Le statut de l'offre est requis")
	@Schema(description = "Statut de l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private TradeStatusDto status;
}
