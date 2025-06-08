package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de détail pour un exportateur.
 * <p>
 * Hérite des propriétés communes définies dans {@link TraderDetailDto} (nom, identifiants,
 * coordonnées, etc.) et est typé "exporter" pour la désérialisation JSON polymorphique.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les exportateurs.")
@JsonTypeName("exporter")
public class ExporterDetailDto extends TraderDetailDto {
}
