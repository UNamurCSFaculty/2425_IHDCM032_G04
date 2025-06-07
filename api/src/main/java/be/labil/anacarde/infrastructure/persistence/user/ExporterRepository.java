package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Exporter;
import be.labil.anacarde.domain.model.User;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data pour l’entité {@link Exporter}.
 * <p>
 * Étend {@code GenericUserRepository} afin de fournir les opérations CRUD
 * et les requêtes spécifiques pour les exportateurs.
 */
@Repository
public interface ExporterRepository extends GenericUserRepository<Exporter> {
	/**
	 * Retourne les utilisateurs triés alphabétiquement par nom de famille.
	 *
	 * @return Une liste triée d'utilisateurs.
	 */
	List<User> findAllByOrderByLastNameAsc();

}
