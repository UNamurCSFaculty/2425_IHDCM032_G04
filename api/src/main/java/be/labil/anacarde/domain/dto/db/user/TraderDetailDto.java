package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO abstrait pour tout utilisateur de type « trader ».
 * <p>
 * Sert de base pour les sous-types spécifiques (producteur, transformateur, exportateur),
 * en fournissant la configuration polymorphique JSON (Jackson) et la documentation OpenAPI.
 * <p>
 * Les implémentations concrètes héritent de {@link UserDetailDto} et sont identifiées
 * par le champ JSON « type » valant l’une des valeurs : « producer », « transformer » ou « exporter ».
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("trader")
@Schema(description = "Objet de transfert de données pour les traders.", subTypes = {
		ProducerDetailDto.class, TransformerDetailDto.class, ExporterDetailDto.class,})
public abstract class TraderDetailDto extends UserDetailDto {
}
