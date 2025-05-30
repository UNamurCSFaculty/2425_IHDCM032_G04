package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert pour créer ou mettre à jour une offres.")
public class BidUpdateDto {

	/** Montant de l'offre. */
	@NotNull(message = "Le montant est requis")
	@Schema(description = "Montant de l'offre", example = "150.75", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal amount;

	/** Date de création de l'offre. */
	@Schema(description = "Date de création de l'offre", example = "2025-04-07T11:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime creationDate;

	/** Enchère associée à l'offre. */
	@NotNull(message = "L'identifiant de l'enchère associée est requise")
	@Schema(description = "Identifiant de l'enchère associée à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer auctionId;

	/** Trader ayant passé l'offre. */
	@NotNull(message = "Le trader est requis")
	@Schema(description = "Trader ayant passé l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer traderId;

	/** Statut de l'offre. */
	@Schema(description = "Statut de l'offre")
	private Integer statusId;
}