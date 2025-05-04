package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO pour l'entité Role.
 */
@Data
@AllArgsConstructor
@Schema(description = "Objet de transfert de données pour les rôles.")
public class RoleDto {

	/** Identifiant unique du rôle. */
	@Schema(description = "Identifiant unique du rôle", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;

	/** Nom du rôle. */
	@Schema(description = "Nom du rôle", example = "ROLE_USER", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom du rôle est requis")
	private String name;
}
