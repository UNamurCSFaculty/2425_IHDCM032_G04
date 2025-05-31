package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.HarvestProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

	boolean existsByIdAndProducerId(Integer productId, Integer producerId);
}
