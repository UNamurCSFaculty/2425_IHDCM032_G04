package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @brief Repository interface for Document entities.
 */
public interface DocumentRepository extends JpaRepository<Document, Integer> {
}
