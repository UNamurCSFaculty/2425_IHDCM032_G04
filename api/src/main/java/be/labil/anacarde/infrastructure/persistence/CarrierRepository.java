package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interface Repository pour les entités Carrier. */
public interface CarrierRepository extends JpaRepository<Carrier, Integer> {

}
