package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
}
