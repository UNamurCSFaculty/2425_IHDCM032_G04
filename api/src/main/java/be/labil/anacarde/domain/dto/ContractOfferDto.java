package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité ContractOffer.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les offres de contrat.")
public class ContractOfferDto {

	/** Identifiant unique de l'offre de contrat. */
	@Schema(description = "Identifiant unique de l'offre de contrat", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Statut de l'offre de contrat. */
	@NotBlank(message = "Le statut est requis")
	@Schema(description = "Statut de l'offre de contrat", example = "En attente", requiredMode = Schema.RequiredMode.REQUIRED)
	private String status;

	/** Prix par kilogramme proposé dans l'offre. */
	@NotNull(message = "Le prix par kg est requis")
	@Schema(description = "Prix par kilogramme proposé", example = "2.50", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal pricePerKg;

	/** Date de création de l'offre de contrat. */
	@NotNull(message = "La date de création est requise")
	@Schema(description = "Date de création de l'offre", example = "2025-04-07T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime creationDate;

	/** Durée de validité de l'offre (en jours). */
	@NotNull(message = "La durée est requise")
	@Schema(description = "Durée de validité de l'offre en jours", example = "30.0", requiredMode = Schema.RequiredMode.REQUIRED)
	private Float duration;

	/** Trader vendeur associé à l'offre. */
	@NotNull(message = "Le vendeur est requis")
	@Schema(description = "Vendeur associé à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private TraderDto seller;

	/** Trader acheteur associé à l'offre. */
	@NotNull(message = "L'acheteur est requis")
	@Schema(description = "Acheteur associé à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private TraderDto buyer;

	/** Qualité associée à l'offre. */
	@NotNull(message = "La qualité est requise")
	@Schema(description = "Qualité associée à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private QualityDto quality;
}
