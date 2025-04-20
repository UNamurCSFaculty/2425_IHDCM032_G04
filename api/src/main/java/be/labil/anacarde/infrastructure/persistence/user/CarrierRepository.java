package be.labil.anacarde.infrastructure.persistence.user;

import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Region;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends GenericUserRepository<Carrier> {

	/**
	 * Recherche les régions associées à un transporteur.
	 *
	 * @param carrierId
	 *            L'ID du transporteur
	 * @return Une liste de régions associées.
	 */
	@Query("SELECT c.regions FROM Carrier c WHERE c.id = :carrierId")
	List<Region> findCarrierRegions(Integer carrierId);
}
