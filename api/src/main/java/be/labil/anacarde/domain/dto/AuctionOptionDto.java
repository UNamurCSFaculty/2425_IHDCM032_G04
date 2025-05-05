package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité ActionOption.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les options d'enchère.")
public class AuctionOptionDto {

	@Schema(description = "Identifiant unique de l'option d'enchère", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;

	@NotBlank(message = "Le nom de l'option d'enchère est requis")
	@Schema(description = "Nom de l'option d'enchère", example = "Option A", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
