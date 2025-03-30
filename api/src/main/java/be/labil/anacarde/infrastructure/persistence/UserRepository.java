package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** Interface Repository pour les entités User. */
public interface UserRepository extends JpaRepository<User, Integer> {
	/**
	 * Recherche une entité User dont l'adresse e-mail concide avec celle fournie.
	 *
	 * @param email
	 *            L'adresse e-mail de l'utilisateur à rechercher.
	 * @return Un Optional contenant le User trouvé, ou Optional.empty() si aucun utilisateur n'est trouvé.
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
	Optional<User> findByEmail(String email);

	@Override
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
	Optional<User> findById(Integer id);

}
