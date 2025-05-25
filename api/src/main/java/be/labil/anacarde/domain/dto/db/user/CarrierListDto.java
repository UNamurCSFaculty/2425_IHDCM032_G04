package be.labil.anacarde.domain.dto.db.user;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Carrier.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les transporteurs.", subTypes = {
		TraderListDto.class, CarrierListDto.class, QualityInspectorListDto.class,
		AdminListDto.class})
@JsonTypeName("carrier")
public class CarrierListDto extends UserListDto {

	/** Prix par kilomètre facturé par le transporteur. */
	@Schema(description = "Prix par kilomètre facturé par le transporteur", example = "1.50", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "Le prix par kilomètre est requis")
	private Double pricePerKm;

	/** Prix par kilomètre facturé par le transporteur. */
	@Schema(description = "Rayon d'action du transporteur en kilomètres", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "Le rayon d'action est requis")
	private Double radius;
}
