package be.labil.anacarde.domain.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * @brief Data Transfer Object for Document entities.
 */
public class DocumentDto {
    /**
     * @brief Unique identifier of the document.
     */
    private Integer id;

    /**
     * @brief Type of the document.
     */
    private String documentType;

    /**
     * @brief Format of the document.
     */
    private String format;

    /**
     * @brief Storage path where the document is located.
     */
    private String storagePath;

    /**
     * @brief Date and time when the document was uploaded.
     */
    private LocalDateTime uploadDate;

    /**
     * @brief Identifier of the user associated with the document.
     */
    private Integer userId;
}
