package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.QualityInspector;
import be.labil.anacarde.domain.model.User;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data pour l’entité {@link QualityInspector}.
 * <p>
 * Étend {@link GenericUserRepository} pour fournir les opérations CRUD de base et des requêtes
 * spécifiques aux inspecteurs qualité.
 */
@Repository
public interface QualityInspectorRepository extends GenericUserRepository<QualityInspector> {
	/**
	 * Récupère tous les inspecteurs qualité triés par ordre alphabétique croissant de leur nom de
	 * famille.
	 * <p>
	 * Utile pour afficher une liste d’inspecteurs dans un ordre lisible pour l’utilisateur.
	 *
	 * @return liste de {@link User} (inspecteurs qualité) triée par nom de famille en ordre
	 *         ascendant
	 */
	List<User> findAllByOrderByLastNameAsc();
}