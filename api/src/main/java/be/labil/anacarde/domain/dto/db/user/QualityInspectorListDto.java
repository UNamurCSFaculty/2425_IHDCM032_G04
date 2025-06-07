package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de liste pour un inspecteur qualité.
 * <p>
 * Hérite des propriétés communes définies dans {@link UserListDto}
 * (identifiant, nom, rôle, etc.) et est typé "quality_inspector"
 * pour la désérialisation JSON polymorphique.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les inspecteurs qualité.")
@JsonTypeName("quality_inspector")
public class QualityInspectorListDto extends UserListDto {
}
