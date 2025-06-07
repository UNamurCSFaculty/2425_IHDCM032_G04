package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de détail pour un inspecteur qualité.
 * <p>
 * Hérite des propriétés communes définies dans {@link UserDetailDto} (nom, identifiants,
 * coordonnées, etc.) et est typé "quality_inspector" pour la désérialisation JSON polymorphique.
 * <p>
 * Utilisé pour représenter un utilisateur de type inspecteur qualité avec toutes ses informations
 * de profil.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les inspecteurs qualité.")
@JsonTypeName("quality_inspector")
public class QualityInspectorDetailDto extends UserDetailDto {
}
