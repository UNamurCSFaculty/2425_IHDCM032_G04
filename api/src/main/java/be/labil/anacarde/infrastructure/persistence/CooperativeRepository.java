package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Cooperative;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CooperativeRepository extends JpaRepository<Cooperative, Integer> {
}
