package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour l'entité Store. Fournit des méthodes pour interagir avec la base de données
 * concernant les magasins.
 */
public interface StoreRepository extends JpaRepository<Store, Integer> {

	/**
	 * Vérifie si un store existe pour un utilisateur donné.
	 *
	 * @param userId
	 *            L'identifiant de l'utilisateur.
	 * @return true si un store existe pour l'utilisateur, false sinon.
	 */
	boolean existsStoreByUserId(Integer userId);
}
