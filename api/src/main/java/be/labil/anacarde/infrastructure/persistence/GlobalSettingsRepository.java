package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l’entité {@link GlobalSettings}.
 * <p>
 * Fournit les opérations CRUD de base pour accéder et modifier
 * la configuration globale de l’application stockée en base de données.
 */
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Integer> {
}