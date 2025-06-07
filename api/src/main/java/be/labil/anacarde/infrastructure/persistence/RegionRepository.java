package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l’entité {@link Region}.
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base
 * ainsi que la pagination et le tri pour les régions géographiques.
 */
public interface RegionRepository extends JpaRepository<Region, Integer> {
}
