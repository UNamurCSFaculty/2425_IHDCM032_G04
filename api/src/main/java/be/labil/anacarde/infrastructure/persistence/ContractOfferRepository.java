package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.ContractOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractOfferRepository extends JpaRepository<ContractOffer, Integer> {
}
