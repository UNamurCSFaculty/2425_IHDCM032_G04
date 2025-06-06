package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Transformer.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les transformateurs.")
@JsonTypeName("transformer")
public class TransformerListDto extends TraderListDto {
	// Aucun champ supplémentaire n'est nécessaire ici.
}
