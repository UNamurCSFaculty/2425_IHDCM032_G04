package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.QualityControl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityControlRepository extends JpaRepository<QualityControl, Integer> {
}
