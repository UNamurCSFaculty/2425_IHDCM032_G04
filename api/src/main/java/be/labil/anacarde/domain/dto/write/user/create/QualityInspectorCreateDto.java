package be.labil.anacarde.domain.dto.write.user.create;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité QualityInspector.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les inspecteurs qualité.")
public class QualityInspectorCreateDto extends UserCreateDto {
	// Aucun champ supplémentaire à déclarer, on hérite de UserDetailDto
}
