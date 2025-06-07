package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Admin;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data pour l’entité {@link Admin}.
 * <p>
 * Étend {@code GenericUserRepository} afin de fournir les opérations CRUD spécifiques aux
 * administrateurs.
 */
@Repository
public interface AdminRepository extends GenericUserRepository<Admin> {
}
