package be.labil.anacarde.domain.dto.write.user.update;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Trader.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les traders.", subTypes = {
		ProducerUpdateDto.class, TransformerUpdateDto.class, ExporterUpdateDto.class,})
public abstract class TraderUpdateDto extends UserUpdateDto {
	// Aucun champ supplémentaire à déclarer, on hérite de UserDto
}
