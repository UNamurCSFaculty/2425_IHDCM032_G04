package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de liste pour les administrateurs.
 * <p>
 * Hérite des propriétés de {@link UserListDto} pour présenter une vue simplifiée
 * des utilisateurs de type "admin" dans les collections.
 * Utilise le type JSON "admin" pour la désérialisation polymorphique.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les administrateurs.")
@JsonTypeName("admin")
public class AdminListDto extends UserListDto {
}