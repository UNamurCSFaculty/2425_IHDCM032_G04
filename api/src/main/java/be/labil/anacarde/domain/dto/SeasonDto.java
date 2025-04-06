package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les saisons agricoles.")
public class SeasonDto {

	@Schema(description = "Identifiant unique de la saison", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@Schema(description = "Date de début de la saison", example = "2024-03-01T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La date de début de la saison est requise")
	private LocalDateTime startDate;

	@Schema(description = "Date de fin de la saison", example = "2024-09-30T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La date de fin de la saison est requise")
	private LocalDateTime endDate;
}
