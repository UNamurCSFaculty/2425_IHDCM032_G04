package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les champs agricoles.")
public class FieldDto {

	@Schema(description = "Identifiant unique du champ", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(description = "Identifiant du producteur associé", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer producerId;

	@Schema(description = "Localisation géographique du champ", example = "POINT(4.3517 50.8503)", requiredMode = Schema.RequiredMode.REQUIRED)
	private String location;

	@Schema(description = "Surface du champ en mètres carrés", example = "10000.50", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal surfaceAreaM2;

	@Schema(description = "Détails supplémentaires sur le sol ou les cultures", example = "Sol argileux, propice aux cultures céréalières", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String details;
}
