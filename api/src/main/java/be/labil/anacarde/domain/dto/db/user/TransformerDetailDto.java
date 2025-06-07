package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de détail pour un transformateur.
 * <p>
 * Hérite des propriétés communes définies dans {@link TraderDetailDto} (identité,
 * coordonnées, etc.) et est typé "transformer" pour la désérialisation JSON polymorphique.
 * <p>
 * Utilisé pour représenter un utilisateur de type transformateur avec ses informations de profil.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les transformateurs.")
@JsonTypeName("transformer")
public class TransformerDetailDto extends TraderDetailDto {
}
