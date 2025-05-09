package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Store.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour un entrepôt (store).")
public class StoreDetailDto extends BaseDto {

	/** Nom du store. */
	@NotNull(message = "Le nom est requis")
	@Schema(description = "Nom du store", example = "Nassara", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	/** Position géographique (coordonnées GPS) du store. */
	@NotNull(message = "La position (location) est requise")
	@Schema(description = "Coordonnées géographiques du store (au format GeoJSON, WKT ou équivalent)", example = "POINT(2.3522 48.8566)", requiredMode = Schema.RequiredMode.REQUIRED)
	private String location;

	/** Identifiant de l'utilisateur propriétaire du store. */
	@NotNull(message = "L'identifiant de l'utilisateur est requis")
	@Schema(description = "Identifiant de l'utilisateur lié au store", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer userId;
}
