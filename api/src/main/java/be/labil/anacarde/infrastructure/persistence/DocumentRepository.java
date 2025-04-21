package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour les documents.
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

}
