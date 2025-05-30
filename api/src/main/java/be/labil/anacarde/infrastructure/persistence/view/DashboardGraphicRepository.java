package be.labil.anacarde.infrastructure.persistence.view;

import be.labil.anacarde.domain.model.DashboardGraphic;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DashboardGraphicRepository extends JpaRepository<DashboardGraphic, LocalDateTime> {

	@Query(value = "SELECT * FROM v_dashboard_graphic", nativeQuery = true)
	List<DashboardGraphic> findAllSeries();
}
