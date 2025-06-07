package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository Spring Data pour l’entité {@link User}.
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base
 * et déclare des méthodes de requête personnalisées pour la recherche,
 * la vérification d’existence et la mise à jour native de la date d’enregistrement.
 */
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
	 * Remplace nativement la date d’enregistrement ({@code registration_date})
	 * d’un utilisateur identifié par son ID.
	 *
	 * @param id      l’identifiant de l’utilisateur à mettre à jour
	 * @param newDate la nouvelle date d’enregistrement à appliquer
	 */
	@Modifying
	@Transactional
	@Query(value = """
			  	UPDATE users
			    SET registration_date = :newDate
			   	WHERE id = :id
			""", nativeQuery = true)
	void overrideCreationDateNative(@Param("id") Integer id,
			@Param("newDate") LocalDateTime newDate);

	/**
	 * Retourne les utilisateurs triés alphabétiquement par nom de famille.
	 *
	 * @return Une liste triée d'utilisateurs.
	 */
	List<User> findAllByOrderByLastNameAsc();

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
	 *            téléphone de l'utilisateur
	 * @return true si un utilisateur existe avec ce numéro de téléphone, false sinon.
	 */
	boolean existsByPhone(String phone);
}
