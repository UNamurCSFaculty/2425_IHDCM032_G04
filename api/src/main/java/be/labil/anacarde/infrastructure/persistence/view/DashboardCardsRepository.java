package be.labil.anacarde.infrastructure.persistence.view;

import be.labil.anacarde.domain.model.DashboardCards;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DashboardCardsRepository extends JpaRepository<DashboardCards, Long> {

	@Query(value = "SELECT * FROM v_dashboard_cards", nativeQuery = true)
	Optional<DashboardCards> fetchCards();
}
