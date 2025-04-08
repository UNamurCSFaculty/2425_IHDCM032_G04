package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Exporter;
import org.springframework.stereotype.Repository;

@Repository
public interface ExporterRepository extends GenericUserRepository<Exporter> {
}
