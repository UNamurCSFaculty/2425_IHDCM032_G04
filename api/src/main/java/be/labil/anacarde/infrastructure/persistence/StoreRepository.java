package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    /**
     * Vérifie si un magasin est associé à un utilisateur.
     *
     * @param storeId l'identifiant du magasin
     * @param userId l'identifiant de l'utilisateur
     * @return true si l'utilisateur est le propriétaire du magasin, false sinon
     */
    boolean existsByIdAndUserId(Integer storeId, Integer userId);
}
