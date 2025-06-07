package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.AuctionStrategy;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data pour l’entité {@link AuctionStrategy}.
 * <p>
 * Étend {@link JpaRepository} pour fournir les opérations CRUD de base
 * sur les stratégies d’enchère configurées en base de données.
 */
public interface AuctionStrategyRepository extends JpaRepository<AuctionStrategy, Integer> {
}
