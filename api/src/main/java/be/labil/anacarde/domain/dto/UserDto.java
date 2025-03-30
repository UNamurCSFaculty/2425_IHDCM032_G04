package be.labil.anacarde.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a user for input and output.
 *
 * @note The password field is write-only, meaning it is accepted during creation/update but will not be returned in
 *       responses.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Data Transfer Object pour l'utilisateur")
public class UserDto extends UserListDto {

	/**
	 * List of roles associated with the user.
	 *
	 * <p>
	 * This field is read-only.
	 */
	@Schema(description = "List of roles for the user", accessMode = Schema.AccessMode.READ_ONLY)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<RoleDto> roles;
}
