package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@Schema(description = "Données nécessaires pour l'application cliente.")
@SuperBuilder
public class ApplicationDataDto {

	@NotNull
	@Schema(description = "Liste des stratégies d'enchères disponibles.", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<LanguageDto> languages;
}
