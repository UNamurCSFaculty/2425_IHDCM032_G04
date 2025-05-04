package be.labil.anacarde.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO pour l'entité News.
 */
@Data
@Schema(description = "Objet de transfert de données pour les articles de presse.")
public class NewsDto {

	/** Identifiant unique de l'article. */
	@Schema(description = "Identifiant unique de l'article", example = "1", accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer id;

	/** Titre de l'article. */
	@Schema(description = "Titre de l'article", example = "Le marché de la noix de cajou en expansion", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "Le titre est requis")
	private String title;

	/** Contenu de l'article. */
	@Schema(description = "Contenu de l'article", example = "Le marché mondial de la noix de cajou continue de croître...")
	private String content;

	/** Date de création de l'article. */
	@Schema(description = "Date de création de l'article", example = "2025-04-07T12:34:56", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime creationDate;

	/** Date de publication de l'article. */
	@Schema(description = "Date de publication de l'article", example = "2025-04-08T08:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La date de publication est requise")
	private LocalDateTime publicationDate;

	/** Catégorie de l'article (DTO imbriqué ou juste l'identifiant selon l'usage). */
	@Schema(description = "Catégorie associée à l'article")
	@NotNull(message = "La catégorie est requise")
	private NewsCategoryDto category;
}
