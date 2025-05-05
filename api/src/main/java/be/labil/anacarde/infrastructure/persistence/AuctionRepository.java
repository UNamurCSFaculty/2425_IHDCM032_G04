package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Auction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	/**
	 * Recherche les enchères actives, selon des paramètres de filtrage. Une enchère est
	 * active si l'entité n'a pas été supprimée de la base de données.
	 *
	 * @param traderId
	 *            Identifiant du trader ayant créé l'enchère.
	 * @param status
	 * 			  Status de l'enchère.
	 *
	 * @return Une liste d'enchères actives.
	 */
	@Query("""
			    SELECT a FROM Auction a
			    WHERE a.active = true
			      AND (:traderId IS NULL OR a.trader.id = :traderId)
			      AND (:status IS NULL OR a.status.name = :status)
			    ORDER BY a.id DESC
			""")
	List<Auction> findByActiveTrueFiltered(@Param("traderId") Integer traderId, @Param("status") String status);
}
