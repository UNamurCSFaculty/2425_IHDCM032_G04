package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.User;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data pour l’entité {@link Carrier}.
 * <p>
 * Étend {@code GenericUserRepository} pour fournir les opérations CRUD de base et des méthodes de
 * requêtage spécifiques aux transporteurs.
 */
@Repository
public interface CarrierRepository extends GenericUserRepository<Carrier> {

	/**
	 * Récupère tous les transporteurs ({@code Carrier}) triés par ordre alphabétique croissant de
	 * leur nom de famille.
	 * <p>
	 * Utile pour afficher une liste paginée ou non, où les transporteurs doivent apparaître dans un
	 * ordre lisible pour l’utilisateur.
	 *
	 * @return liste de {@link User} (transporteurs) triée par nom de famille en ordre ascendant
	 */
	List<User> findAllByOrderByLastNameAsc();

}
