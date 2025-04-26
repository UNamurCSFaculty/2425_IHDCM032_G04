package be.labil.anacarde.domain.dto.user;

import be.labil.anacarde.domain.dto.LanguageDto;
import be.labil.anacarde.domain.dto.RoleDto;
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
@Schema(description = "Data Transfer Object pour un utilisateur avec toutes les informations", requiredProperties = {
		"type"}, subTypes = {TraderDetailDto.class, CarrierDetailDto.class, QualityInspectorDetailDto.class,
				AdminDetailDto.class})
public abstract class UserDetailDto extends UserListDto {

	/** Rôles de l'utilisateur. */
	@Schema(description = "Liste des rôles de l'utilisateur", accessMode = Schema.AccessMode.READ_ONLY)
	private Set<RoleDto> roles;

	/** Identifiant de la langue préférée. */
	@Schema(description = "Identifiant de la langue préférée", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La langue est requise")
	private LanguageDto language;
}
