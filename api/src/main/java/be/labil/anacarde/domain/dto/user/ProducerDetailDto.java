package be.labil.anacarde.domain.dto.user;

import be.labil.anacarde.domain.dto.CooperativeDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ProducerDetailDto extends TraderDetailDto {

	/** Identifiant agricole du producteur. */
	@NotBlank
	@Schema(description = "Identifiant agricole", example = "AGRI123456", requiredMode = Schema.RequiredMode.REQUIRED)
	private String agriculturalIdentifier;

	/** Coopérative à laquelle appartient le producteur. */
	@Schema(description = "Coopérative du producteur")
	@JsonBackReference("coop-producers")
	private CooperativeDto cooperative;
}
