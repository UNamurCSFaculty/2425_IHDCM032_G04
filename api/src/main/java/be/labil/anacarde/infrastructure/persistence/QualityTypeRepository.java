package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.QualityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityTypeRepository extends JpaRepository<QualityType, Integer> {
}
