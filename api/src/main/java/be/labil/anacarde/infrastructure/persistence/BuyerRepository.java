package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interface Repository pour les entités Document. */
public interface BuyerRepository extends JpaRepository<Buyer, Integer> {
}
