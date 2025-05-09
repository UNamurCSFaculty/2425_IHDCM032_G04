package be.labil.anacarde.domain.dto.db;

import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Field.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Objet de transfert de données pour les champs.")
public class FieldDto extends BaseDto {

	@Schema(description = "Identifiant du champ (code unique)", example = "F123")
	private String identifier;

	@Schema(description = "Emplacement géographique du champ (format WKT ou GeoJSON selon l'usage).", example = "POINT(2.35 48.85)")
	private String location;

	@Schema(description = "Producteur associé au champ.")
	private ProducerDetailDto producer;
}
