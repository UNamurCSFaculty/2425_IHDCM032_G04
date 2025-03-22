package be.labil.anacarde.domain.dto;

import be.labil.anacarde.domain.enums.Format;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Objet de transfert de données pour les entités Document.")
public class DocumentDto {

	/** Identifiant unique du document. */
	@Schema(description = "Identifiant unique du document", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Integer id;

	/** Type de document (par exemple, PDF, DOCX, etc.). */
	@Schema(description = "Type de document", example = "PDF", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le type de document est requis")
	private String documentType;

	/** Format du document (par exemple, A4, Lettre, etc.). */
	@Schema(description = "Format du document", example = "A4", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le format du document est requis")
	private String format;

	/** Chemin de stockage du document. */
	@Schema(description = "Chemin de stockage du document", example = "/documents/2025/03/document.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le chemin de stockage est requis")
	private String filePath;

	/** Date et heure de l'envoi du document. */
	@Schema(description = "Date et heure de l'envoi du document", example = "2025-03-13T10:15:30", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime uploadDate;

	/** Identifiant de la certification qualité associée. */
	@Schema(description = "Identifiant de la certification qualité associée", example = "3")
	private Integer qualityCertificationId;

	/** Identifiant de l'utilisateur associé au document. */
	@Schema(description = "Identifiant de l'utilisateur associé au document", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'identifiant de l'utilisateur est requis")
	private Integer userId;
}
