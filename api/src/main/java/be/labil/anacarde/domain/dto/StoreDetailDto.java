package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

/**
 * DTO pour l'entité Store.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les magasins.")
public class StoreDetailDto extends UserDetailDto {

	/** Emplacement géographique du magasin sous forme de Point. */
	@Schema(description = "Emplacement géographique du magasin", example = "POINT(4.3517 50.8503)", requiredMode = Schema.RequiredMode.REQUIRED)
	private Point location;
}
