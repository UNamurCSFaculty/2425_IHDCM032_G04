package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.QualityInspector;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityInspectorRepository extends GenericUserRepository<QualityInspector> {
}