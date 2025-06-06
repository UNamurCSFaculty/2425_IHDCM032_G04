package be.labil.anacarde.domain.dto.db;

import be.labil.anacarde.domain.dto.db.user.TraderDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité Intention.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Représente une intention d'achat d'un trader.")
public class IntentionDto extends BaseDto {

	/** Prix proposé par le trader. */
	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	@Schema(description = "Prix proposé", example = "250.75", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal price;

	/** Date de l'intention. */
	@NotNull
	@Schema(description = "Date de l'intention", example = "2025-04-07T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime date;

	/** Quantité proposée. */
	@NotNull
	@Min(1)
	@Schema(description = "Quantité", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer quantity;

	/** Qualité souhaitée. */
	@NotNull
	@Schema(description = "Qualité du produit", requiredMode = Schema.RequiredMode.REQUIRED)
	private QualityDto quality;

	/** Acheteur (trader) qui propose l’intention. */
	@NotNull
	@Schema(description = "Trader acheteur", requiredMode = Schema.RequiredMode.REQUIRED)
	private TraderDetailDto buyer;
}
