package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.TransformedProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransformedProductRepository extends JpaRepository<TransformedProduct, Integer> {
	List<TransformedProduct> findByTransformerId(Integer producerId);
}
