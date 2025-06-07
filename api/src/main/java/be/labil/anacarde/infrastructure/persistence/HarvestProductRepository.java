package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.HarvestProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository Spring Data JPA pour l’entité {@link HarvestProduct}.
 * <p>
 * Fournit les opérations CRUD de base ainsi que des requêtes
 * spécifiques pour la gestion des produits bruts liés aux producteurs.
 */
public interface HarvestProductRepository extends JpaRepository<HarvestProduct, Integer> {

	/**
	 * Retourne une liste de produits bruts.
	 *
	 * @param producerId
	 *            (optionnel) identifiant du producteur
	 * @return
	 */
	@Query("SELECT h FROM HarvestProduct h WHERE (:producerId IS NULL OR h.producer.id = :producerId)")
	List<HarvestProduct> findByProducerId(Integer producerId);

	/**
	 * Vérifie l’existence d’un produit brut pour un producteur donné.
	 *
	 * @param productId  l’identifiant du produit brut
	 * @param producerId l’identifiant du producteur
	 * @return {@code true} si un tel produit existe pour ce producteur, {@code false} sinon
	 */
	boolean existsByIdAndProducerId(Integer productId, Integer producerId);
}
