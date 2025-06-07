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

/**
 * Repository Spring Data pour l’entité {@link Auction}.
 * <p>
 * Fournit des méthodes de recherche paginée pour filtrer les enchères actives selon différents
 * critères (créateur, participant, statut) et une méthode native pour modifier la date de création.
 */
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

	/**
	 * Vérifie si un trader donné a au moins une enchère créée.
	 *
	 * @param userId
	 *            identifiant du trader
	 * @return {@code true} si au moins une enchère existe pour ce trader, {@code false} sinon
	 */
	boolean existsByTraderId(Integer userId);

	/**
	 * Vérifie si une enchère donnée appartient à un trader donné.
	 *
	 * @param auctionId
	 *            identifiant de l’enchère
	 * @param userId
	 *            identifiant du trader
	 * @return {@code true} si l’enchère existe et appartient au trader, {@code false} sinon
	 */
	boolean existsByIdAndTraderId(Integer auctionId, Integer userId);
}
