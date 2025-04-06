package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Transformer;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interface Repository pour les entités Document. */
public interface TransformerRepository extends JpaRepository<Transformer, Integer> {
}
