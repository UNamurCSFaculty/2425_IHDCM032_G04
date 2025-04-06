package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface Repository pour les entités Document.
 */
public interface SellerRepository extends JpaRepository<Seller, Integer> {

}
