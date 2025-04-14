package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.AuctionStrategy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionStrategyRepository extends JpaRepository<AuctionStrategy, Integer> {
}
