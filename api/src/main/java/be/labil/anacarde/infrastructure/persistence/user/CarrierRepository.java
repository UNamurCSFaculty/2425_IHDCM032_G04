package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.User;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends GenericUserRepository<Carrier> {
	/**
	 * Retourne les utilisateurs triés alphabétiquement par nom de famille.
	 *
	 * @return Une liste triée d'utilisateurs.
	 */
	List<User> findAllByOrderByLastNameAsc();

}
