package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

/** Interface Repository pour les entités User. */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	/**
	 * Recherche une entité User dont l'adresse e-mail concide avec celle fournie.
	 *
	 * @param email
	 *            L'adresse e-mail de l'utilisateur à rechercher.
	 * @return Un Optional contenant le User trouvé, ou Optional.empty() si aucun utilisateur n'est
	 *         trouvé.
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
	Optional<User> findByEmail(String email);

	/**
	 * Recherche une entité User dont le numéro de téléphone correspond à celui fourni.
	 *
	 * @param phone
	 *            Le numéro de téléphone de l'utilisateur à rechercher.
	 * @return Un Optional contenant le User trouvé (avec ses rôles chargés), ou Optional.empty() si
	 *         aucun utilisateur n'est trouvé.
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.phone = :phone")
	Optional<User> findByPhone(String phone);

	@Override
	@NonNull
	@EntityGraph(attributePaths = {"roles", "documents", "address", "address.city",
			"address.region", "language"})
	Optional<User> findById(@NonNull Integer id);
}
