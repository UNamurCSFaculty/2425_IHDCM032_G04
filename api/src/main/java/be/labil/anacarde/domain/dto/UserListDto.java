package be.labil.anacarde.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object pour une liste d'utilisateurs")
public class UserListDto {

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

	/** Date d'enregistrement de l'utilisateur. */
	@Schema(description = "Date d'enregistrement", example = "2025-04-01T09:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime registrationDate;

	/** Date de validation de l'utilisateur. */
	@Schema(description = "Date de validation", example = "2025-04-02T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime validationDate;

	/** Statut d'activation du compte. */
	@Schema(description = "Compte activé", example = "true")
	private boolean enabled;

	/** Adresse postale de l'utilisateur. */
	@Schema(description = "Adresse postale de l'utilisateur", example = "Rue de la Loi 16, 1000 Bruxelles")
	private String address;

	/** Numéro de téléphone de l'utilisateur. */
	@Schema(description = "Numéro de téléphone", example = "+32 475 12 34 56")
	private String phone;

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
}
