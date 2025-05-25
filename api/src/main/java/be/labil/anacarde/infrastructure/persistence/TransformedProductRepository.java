package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.TransformedProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransformedProductRepository extends JpaRepository<TransformedProduct, Integer> {
	/**
	 * Retourne une liste de produits transformés, avec récupération profonde de l'entité.
	 *
	 * @param transformerId
	 *            (optionnel) identifiant du transformateur
	 * @return
	 */
	@Query("""
						SELECT t FROM TransformedProduct t
			LEFT JOIN FETCH t.harvestProducts
			WHERE (:transformerId IS NULL OR t.transformer.id = :transformerId)
			""")
	List<TransformedProduct> findByTransformerId(@Param("transformerId") Integer transformerId);
}
