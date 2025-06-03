package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO pour l'ériture d'un champ (Field).
 */
@Data
@Schema(description = "Objet de transfert pour créer ou mettre à jour un field.")
public class FieldUpdateDto {

	@Schema(description = "Identifiant du champ (code unique)", example = "F123")
	private String identifier;

	@Schema(description = "Adresse du champ", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'adresse est requise")
	private AddressUpdateDto address;

	@Schema(description = "Producteur associé au champ.")
	@NotNull
	private Integer producerId;
}