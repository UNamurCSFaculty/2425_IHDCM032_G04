package be.labil.anacarde.domain.dto.write;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO for updating an existing News article.
 */
@Data
@Schema(description = "Objet de transfert de données pour la mise à jour d'un article de presse.")
public class NewsCreateDto {

	@NotBlank(message = "Le titre est requis")
	@Schema(description = "Nouveau titre de l'article", example = "Le marché de la noix de cajou en pleine évolution", requiredMode = Schema.RequiredMode.REQUIRED)
	private String title;

	@NotBlank(message = "Le contenu est requis")
	@Schema(description = "Nouveau contenu de l'article", example = "Les dernières tendances du marché mondial...", requiredMode = Schema.RequiredMode.REQUIRED)
	private String content;

	@Schema(description = "Nouvelle date de publication de l'article", example = "2025-04-09T10:00:00")
	private LocalDateTime publicationDate;

	@Schema(description = "ID de la nouvelle catégorie associée à l'article", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer categoryId;

	@Schema(description = "Nom du nouvel auteur de l'article", example = "Jane Smith")
	private String authorName;
}
