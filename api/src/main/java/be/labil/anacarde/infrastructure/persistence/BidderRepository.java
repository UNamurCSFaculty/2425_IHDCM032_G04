package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Bidder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidderRepository extends JpaRepository<Bidder, Integer> {
}
