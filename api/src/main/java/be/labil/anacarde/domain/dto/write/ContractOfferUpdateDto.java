package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Objet de transfert pour créer ou mettre à jour une enchère.")
public class ContractOfferUpdateDto {

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

	/** Date de terminaison de l'offre de contrat. */
	@NotNull(message = "La date de terminaison est requise")
	@Schema(description = "Date de terminaison de l'offre", example = "2025-04-07T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime endDate;

	/** Trader vendeur associé à l'offre. */
	@NotNull(message = "Le vendeur est requis")
	@Schema(description = "Vendeur associé à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer sellerId;

	/** Trader acheteur associé à l'offre. */
	@NotNull(message = "L'acheteur est requis")
	@Schema(description = "Acheteur associé à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer buyerId;

	/** Qualité associée à l'offre. */
	@NotNull(message = "La qualité est requise")
	@Schema(description = "Qualité associée à l'offre", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer qualityId;
}
