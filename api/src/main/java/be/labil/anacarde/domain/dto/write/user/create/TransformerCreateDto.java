package be.labil.anacarde.domain.dto.write.user.create;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Transformer.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les transformateurs.")
public class TransformerCreateDto extends TraderCreateDto {
	// Aucun champ supplémentaire n'est nécessaire ici.
}
