package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité AuctionOptionValue.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les valeurs d'option d'enchère.")
public class AuctionOptionValueDto {

	@Schema(description = "Identifiant unique de la valeur d'option d'enchère", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	@NotNull(message = "L'option d'enchère est requise")
	@Schema(description = "Option d'enchère associée", requiredMode = Schema.RequiredMode.REQUIRED)
	private AuctionOptionDto auctionOption;

	@NotBlank(message = "La valeur est requise")
	@Schema(description = "Valeur de l'option", example = "Extra option", requiredMode = Schema.RequiredMode.REQUIRED)
	private String value;
}
