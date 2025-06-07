package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Producer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data pour l’entité {@link Producer}.
 * <p>
 * Étend {@code GenericUserRepository} pour fournir les opérations CRUD de base et des méthodes de
 * requête personnalisées spécifiques aux producteurs.
 */
@Repository
public interface ProducerRepository extends GenericUserRepository<Producer> {
	/**
	 * Recherche un producteur par son identifiant agricole.
	 * <p>
	 * Effectue une jointure FETCH sur la coopérative pour charger l’association en une seule
	 * requête.
	 *
	 * @param agriculturalIdentifier
	 *            l’identifiant agricole du producteur
	 * @return un {@link Optional} contenant le {@link Producer} si trouvé, ou vide sinon
	 */
	@Query("SELECT p FROM Producer p LEFT JOIN FETCH p.cooperative WHERE p.agriculturalIdentifier = :agriculturalIdentifier")
	Optional<Producer> findByAgriculturalIdentifier(String agriculturalIdentifier);

	/**
	 * Récupère tous les producteurs triés par ordre alphabétique croissant de leur nom de famille.
	 *
	 * @return liste de {@link Producer} triée par nom de famille en ordre ascendant
	 */
	List<Producer> findAllByOrderByLastNameAsc();

	/**
	 * Recherche les producteurs appartenant à une coopérative donnée.
	 *
	 * @param cooperativeId
	 *            identifiant de la coopérative
	 * @return liste de {@link Producer} associés à la coopérative
	 */
	List<Producer> findByCooperativeId(Integer cooperativeId);
}
