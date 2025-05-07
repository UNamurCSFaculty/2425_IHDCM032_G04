package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.HarvestProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HarvestProductRepository extends JpaRepository<HarvestProduct, Integer> {
	List<HarvestProduct> findByProducerId(Integer producerId);
}
