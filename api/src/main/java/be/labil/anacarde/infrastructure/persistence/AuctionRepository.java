package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Auction;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	/**
	 * Recherche les enchères actives, selon des paramètres de filtrage. Une enchère est active si
	 * l'entité n'a pas été supprimée de la base de données.
	 *
	 * @param traderId
	 *            (optionnel) Identifiant du trader ayant créé l'enchère.
	 * @param status
	 *            (optionnel) Status de l'enchère.
	 *
	 * @return Une liste d'enchères actives et filtrées.
	 */
	@Query("""
			    SELECT a FROM Auction a
			    WHERE a.active = true
			      AND (:traderId IS NULL OR a.trader.id = :traderId)
			      AND (:status IS NULL OR a.status.name = :status)
			""")
	Page<Auction> findByTraderAndStatus(@Param("traderId") Integer traderId,
			@Param("status") String status, Pageable pageable);

	/**
	 * Recherche les enchères actives, selon des paramètres de filtrage. Une enchère est active si
	 * l'entité n'a pas été supprimée de la base de données.
	 *
	 * @param buyerId
	 *            (obligatoire) Identifiant du trader ayant participé à l'enchère.
	 * @param status
	 *            (optionnel) Status de l'enchère.
	 *
	 * @return Une liste d'enchères actives et filtrées.
	 */
	@Query("""
			   SELECT a FROM Auction a
			   LEFT JOIN Bid b ON b.auctionId = a.id
			   WHERE a.active = true AND b.trader.id = :buyerId AND (:status IS NULL OR b.status.name = :status)
			""")
	Page<Auction> findByBuyerAndStatus(@Param("buyerId") Integer buyerId,
			@Param("status") String status, Pageable pageable);

	@Modifying
	@Transactional
	@Query(value = """
			  	UPDATE auction
			    SET creation_date = :newDate
			   	WHERE id = :id
			""", nativeQuery = true)
	void overrideCreationDateNative(@Param("id") Integer id,
			@Param("newDate") LocalDateTime newDate);

	boolean existsByTraderId(Integer userId);

	boolean existsByIdAndTraderId(Integer auctionId, Integer userId);
}
