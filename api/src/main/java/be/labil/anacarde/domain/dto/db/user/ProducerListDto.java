package be.labil.anacarde.domain.dto.db.user;

import be.labil.anacarde.domain.dto.db.CooperativeDto;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Producer.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les producteurs.")
@JsonTypeName("producer")
public class ProducerListDto extends TraderListDto {

	/** Identifiant agricole du producteur. */
	@NotBlank
	@Schema(description = "Identifiant agricole", example = "AGRI123456", requiredMode = Schema.RequiredMode.REQUIRED)
	private String agriculturalIdentifier;

	/** Coopérative à laquelle appartient le producteur. */
	@NotNull
	@Schema(description = "Coopérative du producteur", requiredMode = Schema.RequiredMode.REQUIRED)
	private CooperativeDto cooperative;
}
