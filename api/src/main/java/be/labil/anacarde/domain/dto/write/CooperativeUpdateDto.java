package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Objet de transfert pour créer ou mettre à jour une coopérative.")
public class CooperativeUpdateDto {
	/** Nom de la coopérative. */
	@NotBlank
	@Schema(description = "Nom de la coopérative", example = "Coopérative des Producteurs de Cajou", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	/** Date de création de la coopérative. */
	@Schema(description = "Date de création", example = "2023-06-15T00:00:00")
	private LocalDateTime creationDate;

	/** Id du Président de la coopérative. */
	@NotNull
	@Schema(description = "Président de la coopérative", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer presidentId;
}
