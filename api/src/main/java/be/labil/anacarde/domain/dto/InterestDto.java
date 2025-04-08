package be.labil.anacarde.domain.dto;

import be.labil.anacarde.domain.dto.user.TraderDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO pour l'entité Interest.
 */
@Data
@Schema(description = "Représente un intérêt exprimé pour une intention.")
public class InterestDto {

	/** Identifiant unique de l'intérêt. */
	@Schema(description = "Identifiant de l'intérêt", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Prix proposé. */
	@NotNull
	@Schema(description = "Prix proposé", example = "1500.00", requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal price;

	/** Date à laquelle l'intérêt a été exprimé. */
	@NotNull
	@Schema(description = "Date de l'intérêt", example = "2025-04-07T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime date;

	/** L’intention à laquelle se rapporte cet intérêt. */
	@NotNull
	@Schema(description = "Intention concernée par cet intérêt", requiredMode = Schema.RequiredMode.REQUIRED)
	private IntentionDto intention;

	/** Le trader (acheteur) ayant exprimé l’intérêt. */
	@NotNull
	@Schema(description = "Acheteur ayant exprimé l'intérêt", requiredMode = Schema.RequiredMode.REQUIRED)
	private TraderDetailDto buyer;
}
