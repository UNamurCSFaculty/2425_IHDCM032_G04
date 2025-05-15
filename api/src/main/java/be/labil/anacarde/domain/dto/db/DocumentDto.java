package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO pour l'entité Document.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Objet de transfert de données pour les entités Document.", requiredProperties = {
		"contentType", "originalFilename", "size", "extension", "storagePath", "userId"})
public class DocumentDto extends BaseDto {

	/** MIME type, ex. : `application/pdf`. */
	@Schema(description = "MIME type du document", example = "application/pdf", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le contentType est requis")
	private String contentType;

	/** Nom original du fichier pour l’affichage. */
	@Schema(description = "Nom original du fichier", example = "facture_mars_2025.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le originalFilename est requis")
	private String originalFilename;

	/** Taille du fichier en octets. */
	@Schema(description = "Taille du fichier en octets", example = "102400", requiredMode = Schema.RequiredMode.REQUIRED)
	@Min(value = 1, message = "La taille doit être >= 1 octet")
	private long size;

	/** Extension (pdf, jpg, …). */
	@Schema(description = "Extension du fichier", example = "pdf", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "L'extension est requise")
	private String extension;

	/** Emplacement final (chemin disque ou clé S3). */
	@Schema(description = "Chemin de stockage du document", example = "s3://bucket/anacarde/docs/2025/03/facture_mars_2025.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le storagePath est requis")
	private String storagePath;

	/** Date et heure de l'upload du document. */
	@Schema(description = "Date et heure de l'envoi du document", example = "2025-03-13T10:15:30", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime uploadDate;

	/** Identifiant de l'utilisateur propriétaire du document. */
	@Schema(description = "Identifiant de l'utilisateur associé au document", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "L'identifiant de l'utilisateur est requis")
	private Integer userId;
}
