package be.labil.anacarde.domain.dto.db.user;

import be.labil.anacarde.domain.dto.db.CooperativeDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de détail pour un producteur.
 * <p>
 * Hérite des propriétés communes définies dans {@link TraderDetailDto} et ajoute des informations
 * spécifiques au producteur telles que son identifiant agricole et sa coopérative.
 * <p>
 * La gestion de l’identité JSON est configurée pour réutiliser la propriété {@code id} afin
 * d’éviter les boucles lors de la sérialisation.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les producteurs.")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonTypeName("producer")
public class ProducerDetailDto extends TraderDetailDto {

	/** Identifiant agricole du producteur. */
	@NotBlank
	@Schema(description = "Identifiant agricole", example = "AGRI123456", requiredMode = Schema.RequiredMode.REQUIRED)
	private String agriculturalIdentifier;

	/** Coopérative à laquelle appartient le producteur. */
	@Schema(description = "Coopérative du producteur")
	private CooperativeDto cooperative;
}
