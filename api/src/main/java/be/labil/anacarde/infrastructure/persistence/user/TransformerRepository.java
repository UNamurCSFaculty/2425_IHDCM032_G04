package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Transformer;
import be.labil.anacarde.domain.model.User;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data pour l’entité {@link Transformer}.
 * <p>
 * Étend {@link GenericUserRepository} pour fournir les opérations CRUD de base et des requêtes
 * spécifiques aux transformateurs.
 */
@Repository
public interface TransformerRepository extends GenericUserRepository<Transformer> {
	/**
	 * Retourne les utilisateurs triés alphabétiquement par nom de famille.
	 *
	 * @return Une liste triée d'utilisateurs.
	 */
	List<User> findAllByOrderByLastNameAsc();

}
