package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Objet de transfert pour créer ou mettre à jour un contrôle qualité.")
public class QualityControlUpdateDto {

	/** Identifiant du contrôle qualité. */
	@NotBlank(message = "L'identifiant est requis")
	@Schema(description = "Identifiant du contrôle qualité", example = "QC-001", requiredMode = Schema.RequiredMode.REQUIRED)
	private String identifier;

	/** Date du contrôle qualité. */
	@NotNull(message = "La date de contrôle est requise")
	@Schema(description = "Date de contrôle qualité", example = "2025-04-07T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime controlDate;

	/** Granularité mesurée lors du contrôle qualité. */
	@NotNull(message = "La granularité est requise")
	@Schema(description = "Granularité mesurée", example = "0.5", requiredMode = Schema.RequiredMode.REQUIRED)
	private Float granularity;

	/** Résultat du test KOR. */
	@NotNull(message = "Le test KOR est requis")
	@Schema(description = "Résultat du test KOR", example = "0.8", requiredMode = Schema.RequiredMode.REQUIRED)
	private Float korTest;

	/** Taux d'humidité mesuré. */
	@NotNull(message = "L'humidité est requise")
	@Schema(description = "Taux d'humidité mesuré", example = "12.5", requiredMode = Schema.RequiredMode.REQUIRED)
	private Float humidity;

	/** Inspecteur qualité ayant réalisé le contrôle. */
	@NotNull(message = "L'inspecteur qualité est requis")
	@Schema(description = "Inspecteur qualité associé", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer qualityInspectorId;

	/** Qualité du produit mesurée. */
	@NotNull(message = "La qualité est requise")
	@Schema(description = "Qualité associée", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer qualityId;

	/** Document associé au contrôle qualité. */
	// TODO add NotNull
	@Schema(description = "Document associé au contrôle qualité")
	private Integer documentId;
}
