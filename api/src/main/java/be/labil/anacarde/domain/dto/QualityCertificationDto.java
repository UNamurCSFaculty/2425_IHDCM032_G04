package be.labil.anacarde.domain.dto;

import be.labil.anacarde.domain.enums.Quality;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les entités QualityCertification.")
public class QualityCertificationDto {

	/** Identifiant unique de la certification qualité. */
	@Schema(description = "Identifiant unique de la certification qualité", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Identifiant du magasin associé à la certification. */
	@Schema(description = "Identifiant du magasin associé à la certification", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer storeId;

	/** Date de la certification qualité. */
	@Schema(description = "Date de la certification qualité", example = "2025-03-13T10:15:30", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime date;

	/** Niveau de qualité attribué. */
	@Schema(description = "Niveau de qualité attribué", example = "A", requiredMode = Schema.RequiredMode.REQUIRED)
	private Quality quality;

	/** Poids total des produits certifiés. */
	@Schema(description = "Poids total des produits certifiés", example = "1000.50", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal weight;

	/** Taux de semences. */
	@Schema(description = "Taux de semences", example = "12.5", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal seeding;

	/** Kernel Outturn Ratio (KOR). */
	@Schema(description = "Kernel Outturn Ratio (KOR)", example = "48.2", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal kor;

	/** Taux d'humidité des produits. */
	@Schema(description = "Taux d'humidité des produits", example = "7.5", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal humidityRate;

	/** Taux de défaut des produits. */
	@Schema(description = "Taux de défaut des produits", example = "2.1", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal defectRate;
}
