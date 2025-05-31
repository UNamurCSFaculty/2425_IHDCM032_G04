package be.labil.anacarde.domain.dto.db;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Représente une liste paginée d'articles d'actualité.")
public class NewsPageDto {

	@Schema(description = "Le contenu de cette page.")
	private List<NewsDto> content;

	@Schema(description = "Le nombre total de pages.")
	private int totalPages;

	@Schema(description = "Le nombre total d'éléments sur toutes les pages.")
	private long totalElements;

	@Schema(description = "Le numéro de la page courante (commence à zéro).")
	private int number;

	@Schema(description = "Le nombre d'éléments dans la page courante.")
	private int size;

	@Schema(description = "Indique s'il s'agit de la première page.")
	private boolean first;

	@Schema(description = "Indique s'il s'agit de la dernière page.")
	private boolean last;

	@Schema(description = "Le nombre d'éléments dans la page courante.")
	private int numberOfElements;

	@Schema(description = "Indique si la page courante est vide.")
	private boolean empty;
}