package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.QualityCertification;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interface Repository pour les entités QualityCertification. */
public interface QualityCertificationRepository extends JpaRepository<QualityCertification, Integer> {
}
