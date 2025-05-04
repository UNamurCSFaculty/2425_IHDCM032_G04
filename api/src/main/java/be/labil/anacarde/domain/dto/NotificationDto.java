package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO pour l'entité Notification.
 */
@Data
@Schema(description = "Objet de transfert de données pour les notifications.")
public class NotificationDto {

	/** Identifiant unique de la notification. */
	@Schema(description = "Identifiant unique de la notification", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;

	/** Message de la notification. */
	@Schema(description = "Message de la notification", example = "Votre commande a été expédiée", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le message est requis")
	private String message;

	/** Type de la notification. */
	@Schema(description = "Type de notification", example = "INFO", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le type est requis")
	private String type;

	/** Date d'envoi de la notification. */
	@Schema(description = "Date d'envoi de la notification", example = "2025-04-01T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime sendDate;

	/** Statut de lecture de la notification. */
	@Schema(description = "Statut de lecture de la notification", example = "UNREAD", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le statut de lecture est requis")
	private String readStatus;

	/** Identifiant de l'utilisateur associé à la notification. */
	@Schema(description = "Identifiant de l'utilisateur associé à la notification", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'identifiant de l'utilisateur est requis")
	private Integer userId;
}
