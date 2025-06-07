package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO abstrait pour tout utilisateur de type « trader » dans les vues en liste.
 * <p>
 * Sert de base pour les sous-types spécifiques (producteur, transformateur, exportateur),
 * permettant la désérialisation polymorphique JSON et la génération de documentation OpenAPI.
 * <p>
 * Les implémentations concrètes héritent de {@link UserListDto} et sont identifiées
 * par le champ JSON « type » valant l’une des valeurs : « producer », « transformer » ou « exporter ».
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("trader")
@Schema(description = "Objet de transfert de données pour les traders.", subTypes = {
		ProducerListDto.class, TransformerListDto.class, ExporterListDto.class,})
public abstract class TraderListDto extends UserListDto {
}
