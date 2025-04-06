package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Exporter;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interface Repository pour les entités Document. */
public interface ExporterRepository extends JpaRepository<Exporter, Integer> {
}
