package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Quality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityRepository extends JpaRepository<Quality, Integer> {
}
