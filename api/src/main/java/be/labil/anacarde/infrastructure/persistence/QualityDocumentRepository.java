package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.QualityDocument;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interface Repository pour les entités Document. */
public interface QualityDocumentRepository extends JpaRepository<QualityDocument, Integer> {
}
