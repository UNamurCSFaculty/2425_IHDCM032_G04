package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	// List<Product> findBy(Integer traderId);
}
