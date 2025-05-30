package be.labil.anacarde.domain.dto.db.view;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Tableau du nombre d'enchères en cours et créées par jour.
 */
@Getter
@Builder
@AllArgsConstructor
public class DashboardGraphicDto {

	/** Date du jour concérné à 00h Local */
	private LocalDateTime date;

	/** Nombre total d'enchères en cours */
	private Long totalOpenAuctions;

	/** Nombre de nouvelles enchères */
	private Long totalNewAuctions;
}
