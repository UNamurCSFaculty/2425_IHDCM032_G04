package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
}
