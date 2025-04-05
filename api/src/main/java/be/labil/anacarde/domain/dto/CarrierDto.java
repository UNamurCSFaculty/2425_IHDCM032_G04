package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les transporteurs.")
public class CarrierDto {

	@Schema(description = "Identifiant unique du transporteur et de l'utilisateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer userId;

	@Schema(description = "Localisation GPS du transporteur", example = "POINT(4.3517 50.8503)", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La localisation GPS est requise")
	private String gpsLocation;

	@Schema(description = "Rayon de livraison en kilomètres", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "Le rayon de livraison est requis")
	private Double kmRange;

	@Schema(description = "Prix par kilomètre", example = "0.5", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "Le prix par kilomètre est requis")
	private Double kmPrice;
}
