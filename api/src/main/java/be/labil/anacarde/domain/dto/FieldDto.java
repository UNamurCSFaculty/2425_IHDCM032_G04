package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

/**
 * DTO pour l'entité Field.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les champs.")
public class FieldDto extends TraderDto {

	@Schema(description = "Identifiant unique du champ.", example = "F123")
	private Integer id;

	@Schema(description = "Identifiant du champ.", example = "field1")
	private String identifier;

	@Schema(description = "Emplacement géographique du champ.")
	private Point location;

	@Schema(description = "Producteur associé au champ.")
	private ProducerDto producer;
}
