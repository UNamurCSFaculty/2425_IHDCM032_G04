package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interface Repository pour les entités Document. */
public interface DocumentRepository extends JpaRepository<Document, Integer> {
}
