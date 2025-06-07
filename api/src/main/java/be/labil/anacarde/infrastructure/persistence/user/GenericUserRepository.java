package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * Repository générique pour les entités {@link User} (et ses sous-types).
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base et ajoute des méthodes de
 * recherche par email et par ID avec chargement des associations nécessaires.
 *
 * @param <T>
 *            le type d’utilisateur géré (ex. Admin, Carrier, Exporter, …)
 */
@NoRepositoryBean
public interface GenericUserRepository<T extends User> extends JpaRepository<T, Integer> {

	/**
	 * Recherche un utilisateur par son adresse email.
	 *
	 * @param email
	 *            l’adresse email de l’utilisateur à rechercher
	 * @return un {@link Optional} contenant l’utilisateur si trouvé, ou vide sinon
	 */
	Optional<T> findByEmail(String email);

	/**
	 * Recherche un utilisateur par son ID en récupérant également son association {@code language}
	 * pour éviter le lazy loading ultérieur.
	 *
	 * @param id
	 *            l’identifiant numérique de l’utilisateur
	 * @return un {@link Optional} contenant l’utilisateur avec ses associations, ou vide si non
	 *         trouvé
	 */
	@EntityGraph(attributePaths = {"language"})
	@Query("SELECT u FROM #{#entityName} u WHERE u.id = :id")
	Optional<T> findByIdWithAssociations(@Param("id") Integer id);
}
