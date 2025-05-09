package be.labil.anacarde.domain.dto.db.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Exporter.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les exportateurs.")
public class ExporterDetailDto extends TraderDetailDto {
	// Aucun champ supplémentaire n'est nécessaire ici.
}
