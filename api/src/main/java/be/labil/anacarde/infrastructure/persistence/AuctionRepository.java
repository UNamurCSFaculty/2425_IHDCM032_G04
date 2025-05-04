package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Auction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	/**
	 * Recherche les enchères actives. Une enchère active est une entité qui n'a pas été supprimée.
	 *
	 * @return Une liste d'enchères actives.
	 */
	List<Auction> findByActiveTrue();

	/**
	 * Recherche les enchères actives d'un trader donné.
	 *
	 * @param traderId
	 *            Identifiant du trader ayant créé l'enchère.
	 *
	 * @return Une liste d'enchères actives.
	 */
	List<Auction> findByTraderIdAndActiveTrue(Integer traderId);
}
