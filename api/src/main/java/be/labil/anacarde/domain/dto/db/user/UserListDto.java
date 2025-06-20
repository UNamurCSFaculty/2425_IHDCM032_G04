package be.labil.anacarde.domain.dto.db.user;

import be.labil.anacarde.domain.dto.db.BaseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO abstrait de liste pour un utilisateur.
 * <p>
 * Définit les propriétés de base affichées dans les vues de type liste pour tous les types
 * d’utilisateurs (admin, producer, transformer, trader, quality_inspector, exporter, carrier) avec
 * gestion polymorphique via OpenAPI.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@Schema(description = "Data Transfer Object pour un utilisateur", requiredProperties = {
		"type"}, discriminatorProperty = "type", discriminatorMapping = {
				@DiscriminatorMapping(value = "admin", schema = AdminListDto.class),
				@DiscriminatorMapping(value = "producer", schema = ProducerListDto.class),
				@DiscriminatorMapping(value = "transformer", schema = TransformerListDto.class),
				@DiscriminatorMapping(value = "trader", schema = TraderListDto.class),
				@DiscriminatorMapping(value = "quality_inspector", schema = QualityInspectorListDto.class),
				@DiscriminatorMapping(value = "exporter", schema = ExporterListDto.class),
				@DiscriminatorMapping(value = "carrier", schema = CarrierListDto.class)}, subTypes = {
						TraderListDto.class, CarrierListDto.class, QualityInspectorListDto.class,
						AdminListDto.class})
public abstract class UserListDto extends BaseDto {

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
	@Pattern(regexp = "^\\+22901\\d{8}$", message = "Le numéro de téléphone doit commencer par +22901 suivi de 8 chiffres (ex: +2290123456789).")
	@Schema(description = "Numéro de téléphone (Bénin, format local à 10 chiffres débutant par 01, ou +229...)", example = "+2290178123456", pattern = "^\\+22901\\d{8}$")
	private String phone;

	/**
	 * Propriété virtuelle pour Swagger. Ce getter n'est pas utilisé par Jackson car il est ignoré,
	 * mais permet d'afficher dans le schéma OpenAPI une propriété "type" avec un exemple et des
	 * valeurs autorisées.
	 */
	@Schema(name = "type", description = "Type d'utilisateur. Valeurs possibles: admin, producer, transformer, quality_inspector, exporter, carrier", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin", allowableValues = {
			"admin", "producer", "transformer", "quality_inspector", "exporter", "carrier",
			"trader"})
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getVirtualTypeForSwagger() {
		return null;
	}
}
