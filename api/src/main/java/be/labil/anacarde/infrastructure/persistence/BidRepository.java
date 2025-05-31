package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Bid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BidRepository extends JpaRepository<Bid, Integer> {
	/**
	 * Recherche les offres correspondant à une enchère donnée.
	 *
	 * @param auctionId
	 *            L'ID de l'enchère correspondante.
	 * @return Une liste d'offres correspondant à l'enchère.
	 */
	List<Bid> findByAuctionId(Integer auctionId);

	@Modifying
	@Transactional
	@Query(value = """
			  	UPDATE bid
			    SET creation_date = :newDate
			   	WHERE id = :id
			""", nativeQuery = true)
	void overrideCreationDateNative(@Param("id") Integer id,
			@Param("newDate") LocalDateTime newDate);

	boolean existsByIdAndTraderId(Integer auctionId, Integer userId);
}
