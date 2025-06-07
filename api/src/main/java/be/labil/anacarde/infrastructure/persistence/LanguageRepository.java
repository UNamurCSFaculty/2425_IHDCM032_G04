package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l’entité {@link Language}.
 * <p>
 * Fournit les opérations CRUD de base (create, read, update, delete) sur les langues supportées par
 * l’application.
 */
@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
}
