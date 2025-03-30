package be.labil.anacarde.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

/**
 * Represents a user for input and output.
 *
 * @note The password field is write-only, meaning it is accepted during creation/update but will not be returned in
 *       responses.
 */
@Data
@Schema(description = "Data Transfer Object pour l'utilisateur")
public class UserDto {

	/** Unique identifier for the user. */
	@Schema(description = "Identifiant de l'utilisateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** User's first name. */
	@Schema(description = "Prénom de l'utilisateur", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le prénom est requis")
	private String firstName;

	/** User's last name. */
	@Schema(description = "Nom de famille de l'utilisateur", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom de famille est requis")
	private String lastName;

	/** User's email address. */
	@Schema(description = "Adresse email de l'utilisateur", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "L'adresse email est requise")
	@Email(message = "Invalid email format")
	private String email;

	/**
	 * User's password.
	 *
	 * <p>
	 * This field is only used during creation and is write-only.
	 */
	@Schema(description = "Mot de passe de l'utilisateur", accessMode = Schema.AccessMode.WRITE_ONLY, example = "p@ssw0rd")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(groups = ValidationGroups.Create.class, message = "Le mot de passe est requis")
	private String password;

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
