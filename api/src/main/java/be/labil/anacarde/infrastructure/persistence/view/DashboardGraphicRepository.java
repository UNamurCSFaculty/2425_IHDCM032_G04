package be.labil.anacarde.infrastructure.persistence.view;

import be.labil.anacarde.domain.model.DashboardGraphic;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository Spring Data pour l’accès aux séries graphiques du tableau de bord.
 * <p>
 * Cette interface étend {@link JpaRepository} pour gérer les entités
 * {@link DashboardGraphic} identifiées par leur {@link LocalDateTime}.
 * Elle fournit une méthode pour récupérer l’ensemble des points de données
 * à afficher dans les graphiques du dashboard, issus de la vue SQL
 * {@code v_dashboard_graphic}.
 */
public interface DashboardGraphicRepository extends JpaRepository<DashboardGraphic, LocalDateTime> {

	/**
	 * Récupère toutes les séries de données graphiques depuis la vue
	 * {@code v_dashboard_graphic}.
	 * <p>
	 * Chaque élément de la liste correspond à un point de la série,
	 * avec la date (clé primaire) et la valeur associée.
	 *
	 * @return liste de {@link DashboardGraphic} représentant la série complète
	 */
	@Query(value = "SELECT * FROM v_dashboard_graphic", nativeQuery = true)
	List<DashboardGraphic> findAllSeries();
}
