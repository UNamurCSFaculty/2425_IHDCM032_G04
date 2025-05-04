package be.labil.anacarde.domain.dto.user;

import be.labil.anacarde.domain.dto.ValidationGroups;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = AdminDetailDto.class, name = "admin"),
		@JsonSubTypes.Type(value = ProducerDetailDto.class, name = "producer"),
		@JsonSubTypes.Type(value = TransformerDetailDto.class, name = "transformer"),
		@JsonSubTypes.Type(value = QualityInspectorDetailDto.class, name = "quality_inspector"),
		@JsonSubTypes.Type(value = ExporterDetailDto.class, name = "exporter"),
		@JsonSubTypes.Type(value = CarrierDetailDto.class, name = "carrier")})
@Schema(description = "Data Transfer Object pour un utilisateur", requiredProperties = {
		"type"}, discriminatorProperty = "type", discriminatorMapping = {
				@DiscriminatorMapping(value = "admin", schema = AdminDetailDto.class),
				@DiscriminatorMapping(value = "producer", schema = ProducerDetailDto.class),
				@DiscriminatorMapping(value = "transformer", schema = TransformerDetailDto.class),
				@DiscriminatorMapping(value = "quality_inspector", schema = QualityInspectorDetailDto.class),
				@DiscriminatorMapping(value = "exporter", schema = ExporterDetailDto.class),
				@DiscriminatorMapping(value = "carrier", schema = CarrierDetailDto.class)}, subTypes = {
						TraderListDto.class, CarrierListDto.class, QualityInspectorListDto.class, AdminListDto.class})
public abstract class UserListDto {

	/** Unique identifier for the user. */
	@Schema(description = "Identifiant de l'utilisateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
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
	private LocalDateTime registrationDate = LocalDateTime.now();

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
	@Pattern(regexp = "^(?:\\+229)?(?:01[2-9]\\d{7}|[2-9]\\d{7})$", message = "Numéro invalide – doit être +229XXXXXXXX ou +22901XXXXXXXX")
	@Schema(description = "Numéro de téléphone (Bénin, ancien et nouveau formats)", example = "+2290197123456", pattern = "^(?:\\+229)?(?:01[2-9]\\d{7}|[2-9]\\d{7})$")
	private String phone;

	/**
	 * User's password.
	 *
	 * This field is only used during creation and is write-only.
	 */
	@Schema(description = "Mot de passe de l'utilisateur", accessMode = Schema.AccessMode.WRITE_ONLY, example = "p@ssw0rd")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(groups = ValidationGroups.Create.class, message = "Le mot de passe est requis")
	@Size(min = 8, message = "Le mot de passe doit contenir au moins {min} caractères", groups = ValidationGroups.Create.class)
	private String password;

	/**
	 * Propriété virtuelle pour Swagger. Ce getter n'est pas utilisé par Jackson car il est ignoré, mais permet
	 * d'afficher dans le schéma OpenAPI une propriété "type" avec un exemple et des valeurs autorisées.
	 */
	@Schema(name = "type", description = "Type d'utilisateur. Valeurs possibles: admin, producer, transformer, quality_inspector, exporter, carrier", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin", allowableValues = {
			"admin", "producer", "transformer", "quality_inspector", "exporter", "carrier"})
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getVirtualTypeForSwagger() {
		return null;
	}
}
