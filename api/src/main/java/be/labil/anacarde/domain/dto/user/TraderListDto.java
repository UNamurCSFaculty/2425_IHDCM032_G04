package be.labil.anacarde.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Trader.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les traders.")
public abstract class TraderListDto extends UserListDto {
	// Aucun champ supplémentaire à déclarer, on hérite de UserDto
}
