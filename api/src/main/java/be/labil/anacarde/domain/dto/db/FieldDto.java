package be.labil.anacarde.domain.dto.db;

import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

	@Schema(description = "Adresse du champ", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'adresse est requise")
	private AddressDto address;

	@Schema(description = "Producteur associé au champ.")
	private ProducerDetailDto producer;
}
