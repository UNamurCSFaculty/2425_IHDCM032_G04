package be.labil.anacarde.domain.dto.write.user.create;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Trader.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les traders.", subTypes = {
		ProducerCreateDto.class, TransformerCreateDto.class, ExporterCreateDto.class,})
public abstract class TraderCreateDto extends UserCreateDto {
	// Aucun champ supplémentaire à déclarer, on hérite de UserDto
}
