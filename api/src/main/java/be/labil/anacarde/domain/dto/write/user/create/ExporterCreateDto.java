package be.labil.anacarde.domain.dto.write.user.create;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Exporter.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les exportateurs.")
public class ExporterCreateDto extends TraderCreateDto {
	// Aucun champ supplémentaire n'est nécessaire ici.
}
