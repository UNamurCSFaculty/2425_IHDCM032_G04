package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Trader.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("trader")
@Schema(description = "Objet de transfert de données pour les traders.", subTypes = {
		ProducerListDto.class, TransformerListDto.class, ExporterListDto.class,})
public abstract class TraderListDto extends UserListDto {
	// Aucun champ supplémentaire à déclarer, on hérite de UserDto
}
