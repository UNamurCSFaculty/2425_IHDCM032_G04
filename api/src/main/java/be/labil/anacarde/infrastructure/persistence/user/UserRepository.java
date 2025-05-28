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
	@Query("SELECT u FROM User u WHERE u.email = :email")
	Optional<User> findByEmail(String email);

	/**
	 * Recherche une entité User dont le numéro de téléphone correspond à celui fourni.
	 *
	 * @param phone
	 *            Le numéro de téléphone de l'utilisateur à rechercher.
	 * @return Un Optional contenant le User trouvé (avec ses rôles chargés), ou Optional.empty() si
	 *         aucun utilisateur n'est trouvé.
	 */
	@Query("SELECT u FROM User u WHERE u.phone = :phone")
	Optional<User> findByPhone(String phone);

	@Override
	@NonNull
	@EntityGraph(attributePaths = {"documents", "address", "address.city", "address.region",
			"language"})
	Optional<User> findById(@NonNull Integer id);

	/**
	 * Vérifie si un utilisateur existe avec l'adresse e-mail fournie.
	 *
	 * @param email
	 *            L'adresse e-mail à vérifier.
	 * @return true si un utilisateur existe avec cette adresse e-mail, false sinon.
	 */
	boolean existsByEmail(String email);

	/**
	 * Vérifie si un utilisateur existe avec le numéro de téléphone fourni.
	 * 
	 * @param phone
	 * @return true si un utilisateur existe avec ce numéro de téléphone, false sinon.
	 */
	boolean existsByPhone(String phone);
}
