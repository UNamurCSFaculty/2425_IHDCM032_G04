package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducerRepository extends GenericUserRepository<Producer> {
	/**
	 * Recherche une entité Producer dont l'identifiant agricole correspond à celui fourni.
	 *
	 * @param agriculturalIdentifier
	 *            L’identifiant agricole du producteur à rechercher.
	 * @return Un Optional contenant le User trouvé (avec ses rôles chargés), ou Optional.empty() si
	 *         aucun utilisateur n’est trouvé.
	 */
	@Query("SELECT p FROM Producer p LEFT JOIN FETCH p.cooperative WHERE p.agriculturalIdentifier = :agriculturalIdentifier")
	Optional<User> findByAgriculturalIdentifier(String agriculturalIdentifier);
}
