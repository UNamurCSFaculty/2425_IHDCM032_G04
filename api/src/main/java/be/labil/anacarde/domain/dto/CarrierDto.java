package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les transporteurs.")
public class CarrierDto {

	@Schema(description = "Identifiant unique du transporteur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(description = "Localisation GPS du transporteur", example = "POINT(4.3517 50.8503)", requiredMode = Schema.RequiredMode.REQUIRED)
	private String gpsLocation;

	@Schema(description = "Rayon de livraison en kilomètres", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal kmRange;

	@Schema(description = "Prix par kilomètre", example = "0.5", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal kmPrice;

	@Schema(description = "Identifiant de l'utilisateur associé au transporteur", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer userId;
}
