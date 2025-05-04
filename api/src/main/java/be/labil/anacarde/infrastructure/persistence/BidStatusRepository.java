package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BidStatusRepository extends JpaRepository<BidStatus, Integer> {

	/**
	 * Retourne le status accepté.
	 *
	 * @return Un status correspondant à l'acceptation d'une offre.
	 */
	@Query("SELECT s FROM BidStatus s WHERE s.name = 'Accepté'")
	BidStatus findStatusAccepted();

	/**
	 * Retourne le status en cours.
	 *
	 * @return Un status correspondant à l'évaluation d'une offre.
	 */
	@Query("SELECT s FROM BidStatus s WHERE s.name = 'En cours'")
	BidStatus findStatusPending();
}
