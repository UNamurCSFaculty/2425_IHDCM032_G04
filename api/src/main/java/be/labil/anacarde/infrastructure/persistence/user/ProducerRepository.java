package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Producer;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducerRepository extends GenericUserRepository<Producer> {
}
