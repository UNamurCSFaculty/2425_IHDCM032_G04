package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de liste pour les producteurs.
 * <p>
 * Hérite des propriétés communes définies dans {@link TraderListDto} et
 * ajoute les informations nécessaires pour l’affichage en liste des
 * producteurs : leur identifiant agricole et l’ID de leur coopérative.
 * <p>
 * Utilise le type JSON "producer" pour la désérialisation polymorphique.
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
	private Integer cooperativeId;
}
