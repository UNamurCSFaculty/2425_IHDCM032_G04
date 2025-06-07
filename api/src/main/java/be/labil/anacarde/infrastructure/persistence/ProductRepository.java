package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l’entité {@link Product}.
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base
 * sur les produits, ainsi que la pagination et le tri.
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
