package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidStatusRepository extends JpaRepository<BidStatus, Integer> {
}
