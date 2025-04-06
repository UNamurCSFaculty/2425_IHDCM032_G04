package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Bidder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface Repository pour les entités Document.
 */
public interface BidderRepository extends JpaRepository<Bidder, Integer> {

}
