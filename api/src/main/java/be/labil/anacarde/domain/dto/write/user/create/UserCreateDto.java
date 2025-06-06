package be.labil.anacarde.domain.dto.write.user.create;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import be.labil.anacarde.domain.validation.StrongPassword;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité User.
 */
@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = AdminCreateDto.class, name = "admin"),
		@JsonSubTypes.Type(value = ProducerCreateDto.class, name = "producer"),
		@JsonSubTypes.Type(value = TransformerCreateDto.class, name = "transformer"),
		@JsonSubTypes.Type(value = QualityInspectorCreateDto.class, name = "quality_inspector"),
		@JsonSubTypes.Type(value = ExporterCreateDto.class, name = "exporter"),
		@JsonSubTypes.Type(value = CarrierCreateDto.class, name = "carrier"),
		@JsonSubTypes.Type(value = TraderCreateDto.class, name = "trader")})
@Schema(description = "Objet de transfert pour créer ou mettre à jour un utilisateur.", requiredProperties = {
		"type"}, discriminatorProperty = "type", discriminatorMapping = {
				@DiscriminatorMapping(value = "admin", schema = AdminCreateDto.class),
				@DiscriminatorMapping(value = "producer", schema = ProducerCreateDto.class),
				@DiscriminatorMapping(value = "transformer", schema = TransformerCreateDto.class),
				@DiscriminatorMapping(value = "quality_inspector", schema = QualityInspectorCreateDto.class),
				@DiscriminatorMapping(value = "exporter", schema = ExporterCreateDto.class),
				@DiscriminatorMapping(value = "carrier", schema = CarrierCreateDto.class),
				@DiscriminatorMapping(value = "trader", schema = TraderCreateDto.class)}, subTypes = {
						TraderCreateDto.class, CarrierCreateDto.class,
						QualityInspectorCreateDto.class, AdminCreateDto.class,
						ExporterCreateDto.class, TransformerCreateDto.class,
						ProducerCreateDto.class})
public abstract class UserCreateDto {

	/** User's first name. */
	@Schema(description = "Prénom de l'utilisateur", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le prénom est requis")
	private String firstName;

	/** User's last name. */
	@Schema(description = "Nom de famille de l'utilisateur", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le nom de famille est requis")
	private String lastName;

	/** User's email address. */
	@Schema(description = "Adresse email de l'utilisateur", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", format = "email")
	@NotBlank(message = "L'adresse email est requise")
	@NotBlank(message = "L'adresse email est requise")
	@Email(message = "Invalid email format")
	private String email;

	/** Date d'enregistrement de l'utilisateur. */
	@Schema(description = "Date d'enregistrement", example = "2025-04-01T09:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime registrationDate = LocalDateTime.now();

	/** Date de validation de l'utilisateur. */
	@Schema(description = "Date de validation", example = "2025-04-02T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime validationDate;

	/** Statut d'activation du compte. */
	@Schema(description = "Compte activé", example = "true")
	private boolean enabled;

	/** Numéro de téléphone de l'utilisateur. */
	@Pattern(regexp = "^\\+22901\\d{8}$", message = "Le numéro de téléphone doit commencer par +22901 suivi de 8 chiffres (ex: +2290123456789).")
	@Schema(description = "Numéro de téléphone (Bénin, format local à 10 chiffres débutant par 01, ou +229...)", example = "+2290178123456", pattern = "^\\+22901\\d{8}$", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String phone;

	/**
	 * User's password.
	 *
	 * This field is only used during creation and is write-only.
	 */
	@Schema(description = "Mot de passe de l'utilisateur", accessMode = Schema.AccessMode.WRITE_ONLY, example = "p@ssw0rd", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(groups = ValidationGroups.Create.class, message = "Le mot de passe est requis")
	@Size(min = 8, message = "Le mot de passe doit contenir au moins {min} caractères", groups = ValidationGroups.Create.class)
	@StrongPassword(message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial")
	private String password;

	/** Identifiant de la langue préférée. */
	@Schema(description = "Identifiant de la langue préférée", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La langue est requise")
	private Integer languageId;

	@Schema(description = "Adresse de l'utilisateur", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'adresse est requise")
	private AddressDto address;

	/*
	 * @ArraySchema(schema = @Schema(type = "string", format = "binary", description =
	 * "Fichiers à uploader", implementation = MultipartFile.class, requiredMode =
	 * Schema.RequiredMode.REQUIRED), minItems = 1)
	 * 
	 * @NotNull(message = "La liste des documents est requise.")
	 * 
	 * @Size(min = 1, message = "Au moins un document doit être fourni.") private
	 * List<MultipartFile> documents;
	 */

	public String getEmail() {
		return email != null ? email.trim().toLowerCase() : null;
	}
}
