package be.labil.anacarde.infrastructure.persistence.view;

import be.labil.anacarde.domain.model.DashboardCards;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository Spring Data pour l’accès à la vue matérialisée ou view SQL
 * {@code v_dashboard_cards} exposant les indicateurs à afficher sur le tableau de bord.
 * <p>
 * Fournit une méthode pour récupérer l’ensemble des métriques agrégées sous
 * forme d’un unique objet {@link DashboardCards}.
 */
public interface DashboardCardsRepository extends JpaRepository<DashboardCards, Long> {

	/**
	 * Exécute une requête native pour lire la vue {@code v_dashboard_cards}
	 * et récupérer les valeurs des différentes cartes du dashboard.
	 *
	 * @return un {@link Optional} contenant l’entité {@link DashboardCards}
	 *         si la vue renvoie des résultats, ou vide sinon
	 */
	@Query(value = "SELECT * FROM v_dashboard_cards", nativeQuery = true)
	Optional<DashboardCards> fetchCards();
}
