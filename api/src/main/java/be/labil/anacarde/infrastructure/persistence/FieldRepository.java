package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository<Field, Integer> {
}
