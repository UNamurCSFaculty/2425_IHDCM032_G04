package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Schema(description = "DTO pour l'entité Adresse.")
@SuperBuilder
@NoArgsConstructor
public class AddressDto {

	@Schema(description = "Rue/Quartier/Numéro", example = "46 Rue de passion")
	private String street;

	@Schema(description = "Coordonnées géographiques du store (au format GeoJSON, WKT ou équivalent)", example = "POINT(2.3522 48.8566)", requiredMode = Schema.RequiredMode.REQUIRED)
	private String location;

	@Schema(description = "Id de la ville", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer cityId;

	// @NotNull(message = "La région est requise")
	@Schema(description = "Id de la région")
	private Integer regionId;
}
