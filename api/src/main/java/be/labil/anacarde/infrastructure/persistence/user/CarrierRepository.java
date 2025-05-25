package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Carrier;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends GenericUserRepository<Carrier> {
}
