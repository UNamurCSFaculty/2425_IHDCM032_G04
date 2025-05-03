package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.QualityControl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityControlRepository extends JpaRepository<QualityControl, Integer> {

	/**
	 * Recherche les contrôls de qualité correspondant à un produit donné.
	 *
	 * @param producerId
	 *            L'ID du produit correspondant.
	 * @return Une liste de control de qualité correspondant au produit.
	 */
	List<QualityControl> findByProductId(Integer producerId);
}
