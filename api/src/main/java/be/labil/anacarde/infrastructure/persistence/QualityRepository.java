package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Quality;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l’entité {@link Quality}.
 * <p>
 * Fournit les opérations CRUD standard ainsi que la pagination et le tri pour les objets de type
 * Quality gérés par l’application.
 */
public interface QualityRepository extends JpaRepository<Quality, Integer> {
}
