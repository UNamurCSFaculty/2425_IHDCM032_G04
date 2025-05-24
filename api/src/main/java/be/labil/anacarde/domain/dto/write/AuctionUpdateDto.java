package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Objet de transfert pour créer ou mettre à jour une enchère.")
public class AuctionUpdateDto {

	@NotNull(message = "Le prix est requis")
	@Schema(description = "Prix de l'enchère", example = "100.50", requiredMode = Schema.RequiredMode.REQUIRED)
	private double price;

	@NotNull(message = "La quantité de produit est requise")
	@Schema(description = "Quantité de produit associée à l'enchère", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer productQuantity;

	@NotNull(message = "La date d'expiration est requise")
	@Schema(description = "Date d'expiration de l'enchère", example = "2025-12-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime expirationDate;

	@NotNull(message = "Le statut actif est requis")
	@Schema(description = "Statut actif de l'enchère", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
	private Boolean active;

	@NotNull(message = "Le produit est requis")
	@Schema(description = "Produit associé à l'enchère", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer productId;

	@NotNull(message = "Le trader est requis")
	@Schema(description = "Trader ayant créé l'enchère", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer traderId;

	@Schema(description = "Statut de l'enchère")
	private Integer statusId;

	@NotNull(message = "Les options d'enchère sont requises")
	@Schema(description = "Options d'enchère")
	private AuctionOptionsUpdateDto options;
}
