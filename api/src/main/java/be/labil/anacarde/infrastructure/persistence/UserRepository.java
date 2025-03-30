package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Interface Repository pour les entités User. */
public interface UserRepository extends JpaRepository<User, Integer> {
	/**
	 * Recherche une entité User dont l'adresse e-mail correspond à celle spécifiée.
	 *
	 * @param email
	 *            L'adresse e-mail de l'utilisateur à rechercher.
	 * @return Un Optional contenant le User trouvé, ou Optional.empty() si aucun utilisateur n'est trouvé.
	 */
	Optional<User> findByEmail(String email);
}
