package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Écriture d'un magasin (store)")
public class StoreUpdateDto {

	/** Nom du store. */
	@NotNull(message = "Le nom est requis")
	@Schema(description = "Nom du store", example = "Nassara", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	/** Identifiant de l'utilisateur propriétaire du store. */
	@NotNull(message = "L'identifiant de l'utilisateur est requis")
	@Schema(description = "Identifiant de l'utilisateur lié au store", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer userId;

	@Schema(description = "Adresse de l'utilisateur", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'adresse est requise")
	private AddressUpdateDto address;
}
