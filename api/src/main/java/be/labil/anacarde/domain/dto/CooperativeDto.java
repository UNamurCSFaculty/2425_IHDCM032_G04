package be.labil.anacarde.domain.dto;

import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO pour l'entité Cooperative.
 */
@Data
@Schema(description = "Représente une coopérative.")
public class CooperativeDto {

	/** Identifiant unique de la coopérative. */
	@Schema(description = "Identifiant de la coopérative", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Nom de la coopérative. */
	@NotBlank
	@Schema(description = "Nom de la coopérative", example = "Coopérative des Producteurs de Cajou", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	/** Adresse de la coopérative. */
	@NotBlank
	@Schema(description = "Adresse de la coopérative", example = "Rue des Cultures, Abidjan", requiredMode = Schema.RequiredMode.REQUIRED)
	private String address;

	/** Date de création de la coopérative. */
	@Schema(description = "Date de création", example = "2023-06-15T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime creationDate;

	/** Président de la coopérative. */
	@NotNull
	@Schema(description = "Président de la coopérative", requiredMode = Schema.RequiredMode.REQUIRED)
	private ProducerDetailDto president;
}
