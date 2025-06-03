package be.labil.anacarde.domain.dto.db.user;

import be.labil.anacarde.domain.dto.db.AddressDto;
import be.labil.anacarde.domain.dto.db.DocumentDto;
import be.labil.anacarde.domain.dto.db.LanguageDto;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité User.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Data Transfer Object pour un utilisateur", requiredProperties = {
		"type"}, discriminatorProperty = "type", discriminatorMapping = {
				@DiscriminatorMapping(value = "admin", schema = AdminDetailDto.class),
				@DiscriminatorMapping(value = "producer", schema = ProducerDetailDto.class),
				@DiscriminatorMapping(value = "trader", schema = TraderDetailDto.class),
				@DiscriminatorMapping(value = "transformer", schema = TransformerDetailDto.class),
				@DiscriminatorMapping(value = "quality_inspector", schema = QualityInspectorDetailDto.class),
				@DiscriminatorMapping(value = "exporter", schema = ExporterDetailDto.class),
				@DiscriminatorMapping(value = "carrier", schema = CarrierDetailDto.class)}, subTypes = {
						TraderDetailDto.class, CarrierDetailDto.class,
						QualityInspectorDetailDto.class, AdminDetailDto.class})
public abstract class UserDetailDto extends UserListDto {

	/** Identifiant de la langue préférée. */
	@Schema(description = "Identifiant de la langue préférée", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La langue est requise")
	private LanguageDto language;

	@Schema(description = "Adresse de l'utilisateur", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'adresse est requise")
	private AddressDto address;

	@Schema(description = "Détails complets d’un utilisateur")
	private List<DocumentDto> documents;

	@Schema(description = "Indique si l'utilisateur est associé à un store")
	private boolean isStoreAssociated = false;
}
