package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.QualityType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l’entité {@link QualityType}.
 * <p>
 * Étend {@link JpaRepository} afin de fournir les opérations CRUD standard (création, lecture, mise
 * à jour, suppression) ainsi que la pagination et le tri pour les types de qualité définis dans
 * l’application.
 */
public interface QualityTypeRepository extends JpaRepository<QualityType, Integer> {
}
