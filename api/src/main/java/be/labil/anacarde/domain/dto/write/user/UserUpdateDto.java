package be.labil.anacarde.domain.dto.write.user;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.dto.db.RoleDto;
import be.labil.anacarde.domain.dto.db.ValidationGroups;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO pour l'entité User.
 */
@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = AdminUpdateDto.class, name = "admin"),
		@JsonSubTypes.Type(value = ProducerUpdateDto.class, name = "producer"),
		@JsonSubTypes.Type(value = TransformerUpdateDto.class, name = "transformer"),
		@JsonSubTypes.Type(value = QualityInspectorUpdateDto.class, name = "quality_inspector"),
		@JsonSubTypes.Type(value = ExporterUpdateDto.class, name = "exporter"),
		@JsonSubTypes.Type(value = CarrierUpdateDto.class, name = "carrier"),
		@JsonSubTypes.Type(value = TraderUpdateDto.class, name = "trader")})
@Schema(description = "Objet de transfert pour créer ou mettre à jour un utilisateur.", requiredProperties = {
		"type"}, discriminatorProperty = "type", discriminatorMapping = {
				@DiscriminatorMapping(value = "admin", schema = AdminUpdateDto.class),
				@DiscriminatorMapping(value = "producer", schema = ProducerUpdateDto.class),
				@DiscriminatorMapping(value = "transformer", schema = TransformerUpdateDto.class),
				@DiscriminatorMapping(value = "quality_inspector", schema = QualityInspectorUpdateDto.class),
				@DiscriminatorMapping(value = "exporter", schema = ExporterUpdateDto.class),
				@DiscriminatorMapping(value = "carrier", schema = CarrierUpdateDto.class),
				@DiscriminatorMapping(value = "trader", schema = TraderUpdateDto.class)}, subTypes = {
						TraderUpdateDto.class, CarrierUpdateDto.class,
						QualityInspectorUpdateDto.class, AdminUpdateDto.class,
						ExporterUpdateDto.class, TransformerUpdateDto.class,
						ProducerUpdateDto.class})
public abstract class UserUpdateDto {

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
	private LocalDateTime registrationDate = LocalDateTime.now();

	/** Date de validation de l'utilisateur. */
	@Schema(description = "Date de validation", example = "2025-04-02T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime validationDate;

	/** Statut d'activation du compte. */
	@Schema(description = "Compte activé", example = "true")
	private boolean enabled;

	/** Numéro de téléphone de l'utilisateur. */
	@Pattern(regexp = "^(?:\\+229)?01\\d{8}$", message = "Numéro invalide – doit être +229XXXXXXXX ou +22901XXXXXXXX")
	@Schema(description = "Numéro de téléphone (Bénin, format local à 10 chiffres débutant par 01, ou +229...)", example = "+2290178123456", pattern = "^(?:\\+229)?01\\d{8}$")
	private String phone;

	/**
	 * User's password.
	 *
	 * This field is only used during creation and is write-only.
	 */
	@Schema(description = "Mot de passe de l'utilisateur", accessMode = Schema.AccessMode.WRITE_ONLY, example = "p@ssw0rd")
	@NotBlank(groups = ValidationGroups.Create.class, message = "Le mot de passe est requis")
	@Size(min = 8, message = "Le mot de passe doit contenir au moins {min} caractères", groups = ValidationGroups.Create.class)
	private String password;

	/** Rôles de l'utilisateur. */
	@Schema(description = "Liste des rôles de l'utilisateur")
	private Set<RoleDto> roles;

	/** Identifiant de la langue préférée. */
	@Schema(description = "Identifiant de la langue préférée", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La langue est requise")
	private Integer languageId;

	@Schema(description = "Adresse de l'utilisateur", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'adresse est requise")
	private AddressDto address;
	/*
	 * @ArraySchema( schema = @Schema(type = "string", format = "binary", description =
	 * "Fichiers à uploader") )
	 */
	@Schema(description = "Fichiers à uploader", implementation = MultipartFile[].class)
	private List<MultipartFile> documents;
}
