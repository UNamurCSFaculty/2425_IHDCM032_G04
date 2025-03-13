package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for Document entities.")
public class DocumentDto {

    /** Unique identifier of the document. */
    @Schema(
            description = "Unique identifier of the document",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    /** Type of the document. */
    @Schema(
            description = "Type of the document",
            example = "PDF",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String documentType;

    /** Format of the document. */
    @Schema(
            description = "Format of the document",
            example = "A4",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String format;

    /** Storage path where the document is located. */
    @Schema(
            description = "Storage path where the document is located",
            example = "/documents/2025/03/document.pdf",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String storagePath;

    /** Date and time when the document was uploaded. */
    @Schema(
            description = "Date and time when the document was uploaded",
            example = "2025-03-13T10:15:30",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime uploadDate;

    /** Identifier of the user associated with the document. */
    @Schema(
            description = "Identifier of the user associated with the document",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer userId;
}
