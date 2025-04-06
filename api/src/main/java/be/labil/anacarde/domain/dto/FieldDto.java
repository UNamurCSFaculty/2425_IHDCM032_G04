package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les champs agricoles.")
public class FieldDto {

	@Schema(description = "Identifiant unique du champ", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(description = "Identifiant du producteur associé", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'identifiant du producteur est requis")
	private Integer producerId;

	@Schema(description = "Localisation géographique du champ", example = "POINT(4.3517 50.8503)", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "La localisation géographique du champ est requise")
	private String location;

	@Schema(description = "Surface du champ en mètres carrés", example = "10000.50", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La surface du champ est requise")
	private Double surfaceAreaM2;

	@Schema(description = "Détails supplémentaires sur le sol ou les cultures", example = "Sol argileux, propice aux cultures céréalières", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String details;
}
