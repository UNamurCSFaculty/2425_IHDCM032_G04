package be.labil.anacarde.domain.dto;

import be.labil.anacarde.domain.model.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité User.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Data Transfer Object pour l'utilisateur")
public class UserDetailDto extends UserListDto {

	/** Rôles de l'utilisateur. */
	@Schema(description = "Liste des rôles de l'utilisateur", accessMode = Schema.AccessMode.READ_ONLY)
	private Set<RoleDto> roles;

	/** Identifiant de la langue préférée. */
	@Schema(description = "Identifiant de la langue préférée", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La langue est requise")
	private Language language;
}
