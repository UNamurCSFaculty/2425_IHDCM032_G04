package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.QualityControl;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l’entité {@link QualityControl}.
 * <p>
 * Fournit les opérations CRUD de base ainsi que la pagination et le tri
 * pour les objets de contrôle qualité.
 */
public interface QualityControlRepository extends JpaRepository<QualityControl, Integer> {
}
