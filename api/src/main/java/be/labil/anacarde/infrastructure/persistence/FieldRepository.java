package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Field;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l’entité {@link Field}.
 * <p>
 * Fournit les opérations CRUD de base et une méthode de recherche personnalisée
 * pour récupérer les champs cultivés par un producteur spécifique.
 */
public interface FieldRepository extends JpaRepository<Field, Integer> {
	/**
	 * Recherche les Champs correspondant à un utilisateur donné.
	 *
	 * @param userId
	 *            L'ID de l'utilisateur correspondant.
	 * @return Une liste de Champs correspondant à l'utilisateur.
	 */
	List<Field> findByProducerId(Integer userId);
}
