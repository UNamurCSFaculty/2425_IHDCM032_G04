package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de liste pour les exportateurs.
 * <p>
 * Hérite des propriétés communes définies dans {@link TraderDetailDto}
 * (identifiant, nom, coordonnées, etc.) et est typé "exporter" pour la
 * désérialisation JSON polymorphique.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les exportateurs.")
@JsonTypeName("exporter")
public class ExporterListDto extends TraderDetailDto {
}
