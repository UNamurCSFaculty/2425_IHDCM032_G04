package be.labil.anacarde.application.service.export;

import be.labil.anacarde.domain.dto.db.view.DashboardCardsDto;
import be.labil.anacarde.domain.dto.db.view.DashboardGraphicDto;
import java.util.List;

/**
 * Services d’accès aux indicateurs du tableau de bord.
 *
 * <ul>
 * <li><strong>v_dashboard_cards</strong> — KPI « cards » (1 ligne)</li>
 * <li><strong>v_dashboard_graphic</strong> — série chronologique (1 ligne / jour)</li>
 * </ul>
 */
public interface DashboardService {

	/** Récupère la ligne unique de KPI pour alimenter les cartes. */
	DashboardCardsDto getDashboardStats();

	/** Récupère toute la série chronologique pour la courbe « Open vs New ». */
	List<DashboardGraphicDto> getDashboardGraphicSeries();
}
