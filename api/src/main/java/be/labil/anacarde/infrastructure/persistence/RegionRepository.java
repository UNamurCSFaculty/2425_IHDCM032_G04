package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Integer> {
}
