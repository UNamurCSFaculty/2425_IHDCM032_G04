package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Transformer;
import org.springframework.stereotype.Repository;

@Repository
public interface TransformerRepository extends GenericUserRepository<Transformer> {
}
