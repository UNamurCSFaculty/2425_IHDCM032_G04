package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

/**
 * DTO pour l'entité User.
 */
@Data
@Schema(description = "Objet de transfert de données pour les utilisateurs.")
public class UserDto {

	/** Identifiant unique de l'utilisateur. */
	@Schema(description = "Identifiant unique de l'utilisateur", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Nom de l'utilisateur. */
	@Schema(description = "Nom de l'utilisateur", example = "Dupont", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom est requis")
	private String lastName;

	/** Prénom de l'utilisateur. */
	@Schema(description = "Prénom de l'utilisateur", example = "Jean", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le prénom est requis")
	private String firstName;

	/** Adresse email de l'utilisateur. */
	@Schema(description = "Adresse email de l'utilisateur", example = "jean.dupont@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "L'adresse email est requise")
	@Email(message = "Adresse email invalide")
	private String email;

	/** Date d'enregistrement de l'utilisateur. */
	@Schema(description = "Date d'enregistrement", example = "2025-04-01T09:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime registrationDate;

	/** Date de validation de l'utilisateur. */
	@Schema(description = "Date de validation", example = "2025-04-02T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime validationDate;

	/** Statut d'activation du compte. */
	@Schema(description = "Compte activé", example = "true")
	private boolean active;

	/** Adresse postale de l'utilisateur. */
	@Schema(description = "Adresse postale de l'utilisateur", example = "Rue de la Loi 16, 1000 Bruxelles")
	private String address;

	/** Numéro de téléphone de l'utilisateur. */
	@Schema(description = "Numéro de téléphone", example = "+32 475 12 34 56")
	private String phone;

	/** Identifiant de la langue préférée. */
	@Schema(description = "Identifiant de la langue préférée", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La langue est requise")
	private Integer languageId;

	/** Rôles de l'utilisateur. */
	@Schema(description = "Liste des rôles de l'utilisateur", accessMode = Schema.AccessMode.READ_ONLY)
	private Set<RoleDto> roles;

	// TEMPORARY TO BE MODIFIED ---> See UserServiceImpl
	public String getPassword() {
		return "TEST";
	}

	public void setPassword(String encode) {
	}
}
