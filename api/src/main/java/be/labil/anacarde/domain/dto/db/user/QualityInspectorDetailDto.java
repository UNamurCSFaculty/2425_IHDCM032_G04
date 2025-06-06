package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité QualityInspector.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les inspecteurs qualité.")
@JsonTypeName("quality_inspector")
public class QualityInspectorDetailDto extends UserDetailDto {
	// Aucun champ supplémentaire à déclarer, on hérite de UserDetailDto
}
