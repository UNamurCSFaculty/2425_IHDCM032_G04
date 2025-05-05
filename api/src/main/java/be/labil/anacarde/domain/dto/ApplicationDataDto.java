package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "Données nécessaires pour l'application cliente.")
@Builder
public class ApplicationDataDto {

	@NotNull
	@Schema(description = "Liste des stratégies d'enchères disponibles.", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<LanguageDto> languages;
}
