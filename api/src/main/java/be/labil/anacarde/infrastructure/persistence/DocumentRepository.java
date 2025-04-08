package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Interface Repository pour les entités Document. */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
}
