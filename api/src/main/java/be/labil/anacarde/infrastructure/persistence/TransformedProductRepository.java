package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.TransformedProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransformedProductRepository extends JpaRepository<TransformedProduct, Integer> {
	List<TransformedProduct> findByTransformerId(Integer producerId);

	@Query("SELECT t FROM TransformedProduct t LEFT JOIN FETCH t.harvestProducts")
	List<TransformedProduct> findWithHarvestProducts();
}
