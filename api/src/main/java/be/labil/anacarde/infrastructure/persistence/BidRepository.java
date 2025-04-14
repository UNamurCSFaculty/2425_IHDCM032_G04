package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Bid;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Integer> {
	/**
	 * Recherche les offres correspondant à une enchère donnée.
	 *
	 * @param auctionId
	 *            L'ID de l'enchère correspondante.
	 * @return Une liste d'offres correspondant à l'enchère.
	 */
	List<Bid> findByAuctionId(Integer auctionId);
}
