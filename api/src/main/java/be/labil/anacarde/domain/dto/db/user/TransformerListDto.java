package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de liste pour les transformateurs.
 * <p>
 * Hérite des propriétés communes définies dans {@link TraderListDto} (identifiant,
 * rôle, etc.) et est typé "transformer" pour la désérialisation JSON polymorphique.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les transformateurs.")
@JsonTypeName("transformer")
public class TransformerListDto extends TraderListDto {
}
