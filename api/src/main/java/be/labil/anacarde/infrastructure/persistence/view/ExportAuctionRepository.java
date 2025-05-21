package be.labil.anacarde.infrastructure.persistence.view;

import be.labil.anacarde.domain.model.ExportAuction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Accès en lecture seule à la vue SQL <code>v_auction_bid_analysis</code>. Les requêtes natives
 * retournent l’entité <code>ExportAuction</code>.
 */
public interface ExportAuctionRepository extends JpaRepository<ExportAuction, Integer> {

	/** Toutes les lignes de la vue, sans filtre */
	@Query(value = "SELECT * FROM v_auction_bid_analysis", nativeQuery = true)
	List<ExportAuction> findAllView();

	@Query(value = """
			SELECT *
			FROM   v_auction_bid_analysis
			WHERE  auction_id = :id
			""", nativeQuery = true)
	Optional<ExportAuction> findByAuctionId(@Param("id") Integer id);

	@Query(value = """
			SELECT *
			FROM   v_auction_bid_analysis
			WHERE  auction_start_date BETWEEN :start AND :end
			  AND  (:onlyEnded = false OR auction_ended = true)
			""", nativeQuery = true)
	List<ExportAuction> findAllByStartDateBetween(@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("onlyEnded") boolean onlyEnded);
}
