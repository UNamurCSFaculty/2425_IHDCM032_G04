package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Document;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l’entité {@link Document}.
 * <p>
 * Fournit les opérations CRUD de base ainsi que des méthodes de recherche centrées sur la relation
 * entre documents et utilisateurs.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

	/**
	 * Récupère les documents associés à un utilisateur spécifique.
	 *
	 * @param userId
	 *            L'identifiant de l'utilisateur.
	 * @return Liste des documents liés à l'utilisateur.
	 */
	List<Document> findByUserId(Integer userId);

	/**
	 * Recherche un document par son ID et par l’ID de son propriétaire.
	 *
	 * @param id
	 *            identifiant du document à rechercher
	 * @param ownerId
	 *            identifiant de l’utilisateur propriétaire
	 * @return un {@link Optional} contenant le document si trouvé, sinon vide
	 */
	Optional<Document> findByIdAndUserId(Integer id, Integer ownerId);

	/**
	 * Vérifie l’existence d’un document pour un utilisateur donné.
	 *
	 * @param docId
	 *            identifiant du document
	 * @param userId
	 *            identifiant de l’utilisateur censé posséder le document
	 * @return {@code true} si un tel document existe pour cet utilisateur, {@code false} sinon
	 */
	boolean existsByIdAndUserId(Integer docId, Integer userId);
}
