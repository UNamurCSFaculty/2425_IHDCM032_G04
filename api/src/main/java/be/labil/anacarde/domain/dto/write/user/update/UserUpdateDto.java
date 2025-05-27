package be.labil.anacarde.domain.dto.write.user.update;

import be.labil.anacarde.domain.dto.db.ValidationGroups;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	@Schema(description = "Prénom de l'utilisateur", example = "John")
	private String firstName;

	/** User's last name. */
	@Schema(description = "Nom de famille de l'utilisateur", example = "Doe")
	private String lastName;

	/** User's email address. */
	@Schema(description = "Adresse email de l'utilisateur", example = "john.doe@example.com", type = "string", format = "email")
	private String email;

	/** Statut d'activation du compte. */
	@Schema(description = "Compte activé", example = "true")
	private boolean enabled;

	/** Numéro de téléphone de l'utilisateur. */
	@Pattern(regexp = "^(?:\\+229)?01\\d{8}$", message = "Numéro invalide – doit être +229XXXXXXXX ou +22901XXXXXXXX")
	@Schema(description = "Numéro de téléphone (Bénin, format local à 10 chiffres débutant par 01, ou +229...)", example = "+2290178123456", pattern = "^(?:\\+229)?01\\d{8}$")
	private String phone;

	@Schema(description = "Mot de passe de l'utilisateur", accessMode = Schema.AccessMode.WRITE_ONLY, example = "p@ssw0rd")
	@Size(min = 8, message = "Le mot de passe doit contenir au moins {min} caractères", groups = ValidationGroups.Create.class)
	private String password;

	/** Identifiant de la langue préférée. */
	@Schema(description = "Identifiant de la langue préférée")
	private Integer languageId;

	@Schema(description = "Adresse de l'utilisateur")
	private AddressUpdateDto address;

	public String getEmail() {
		return email != null ? email.trim().toLowerCase() : null;
	}
}
