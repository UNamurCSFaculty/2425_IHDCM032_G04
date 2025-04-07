package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité AuctionOption.
 */
@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les options d'enchère.")
public class AuctionOptionDto {

	/** Identifiant unique de l'option d'enchère. */
	@Schema(description = "Identifiant unique de l'option d'enchère", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Nom de l'option d'enchère. */
	@NotBlank(message = "Le nom de l'option d'enchère est requis")
	@Schema(description = "Nom de l'option d'enchère", example = "Option A", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
