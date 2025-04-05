package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface Repository pour les entités Store.
 */
public interface StoreRepository extends JpaRepository<Store, Integer> {
}
