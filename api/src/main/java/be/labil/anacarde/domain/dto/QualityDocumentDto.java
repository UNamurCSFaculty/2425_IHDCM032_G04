package be.labil.anacarde.domain.dto;

import be.labil.anacarde.domain.enums.Format;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Objet de transfert de données pour les documents de qualité.")
public class QualityDocumentDto {

	/** Identifiant unique du document. */
	@Schema(description = "Identifiant unique du document", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Nom du document. */
	@Schema(description = "Nom du document", example = "Rapport_Qualité_2025", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	/** Format du document (Enum: IMAGE, TEXT). */
	@Schema(description = "Format du document", example = "IMAGE", requiredMode = Schema.RequiredMode.REQUIRED)
	private Format format;

	/** Chemin de stockage du document. */
	@Schema(description = "Chemin de stockage du document", example = "/documents/2025/03/document.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
	private String filePath;

	/** Date et heure de l'envoi du document. */
	@Schema(description = "Date et heure de l'envoi du document", example = "2025-03-13T10:15:30", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime uploadDate;

	/** Identifiant de la certification qualité associée. */
	@Schema(description = "Identifiant de la certification qualité associée", example = "3")
	private Integer qualityCertificationId;

	/** Identifiant de l'utilisateur associé au document. */
	@Schema(description = "Identifiant de l'utilisateur associé au document", example = "1")
	private Integer userId;
}
