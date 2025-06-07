package be.labil.anacarde.domain.dto.db.user;

import be.labil.anacarde.domain.dto.db.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * DTO minimaliste représentant les informations de base d’un utilisateur.
 * <p>
 * Utilisé pour les cas où seules les données essentielles
 * (prénom et nom) sont nécessaires, sans détails complémentaires.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Données minimales d'un utilisateur.")
@SuperBuilder
public class UserMiniDto extends BaseDto {

	/** User's first name. */
	@Schema(description = "Prénom de l'utilisateur", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le prénom est requis")
	private String firstName;

	/** User's last name. */
	@Schema(description = "Nom de famille de l'utilisateur", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom de famille est requis")
	private String lastName;
}
