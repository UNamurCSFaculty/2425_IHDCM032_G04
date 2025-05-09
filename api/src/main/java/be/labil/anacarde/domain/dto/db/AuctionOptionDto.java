package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité ActionOption.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les options d'enchère.")
public class AuctionOptionDto extends BaseDto {

	@NotBlank(message = "Le nom de l'option d'enchère est requis")
	@Schema(description = "Nom de l'option d'enchère", example = "Option A", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
}
