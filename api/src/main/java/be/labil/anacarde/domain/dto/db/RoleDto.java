package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO pour l'entité Role.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "Objet de transfert de données pour les rôles.")
public class RoleDto extends BaseDto {

	/** Nom du rôle. */
	@Schema(description = "Nom du rôle", example = "ROLE_USER", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom du rôle est requis")
	private String name;
}
