package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de détail pour un administrateur.
 * <p>
 * Hérite des propriétés communes définies dans {@link UserDetailDto} et est identifiée par le type
 * JSON "admin" pour la désérialisation polymorphique.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les administrateurs.")
@JsonTypeName("admin")
public class AdminDetailDto extends UserDetailDto {
}
