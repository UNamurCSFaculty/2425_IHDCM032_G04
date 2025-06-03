package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.ContractOffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ContractOfferRepository extends JpaRepository<ContractOffer, Integer> {

	/**
	 * Recherche les offres de contrat dans lesquelles le trader intervient, que ce soit en tant que
	 * vendeur ou acheteur.
	 *
	 * @param traderId
	 *            Identifiant du trader (acheteur ou vendeur).
	 * @return Une liste d'offres de contrat associées à ce trader.
	 */
	@Query("SELECT co FROM ContractOffer co WHERE co.seller.id = :traderId OR co.buyer.id = :traderId")
	List<ContractOffer> findBySellerOrBuyerId(@Param("traderId") Integer traderId);

	/**
	 * Recherche une offre de contrat selon l'ID de la qualité, du vendeur et de l'acheteur.
	 *
	 * @param qualityId
	 *            ID de la qualité.
	 * @param sellerId
	 *            ID du vendeur.
	 * @param buyerId
	 *            ID de l'acheteur.
	 * @param status
	 *            status du contrat
	 * @return Une Optional contenant l'offre de contrat correspondante, si elle existe.
	 */
	@Query("""
			SELECT c FROM ContractOffer c
			WHERE c.quality.id = :qualityId
			  AND c.seller.id = :sellerId
			  AND c.buyer.id = :buyerId
			  AND c.status = :status
			  AND CURRENT_TIMESTAMP BETWEEN c.creationDate AND c.endDate
			ORDER BY c.creationDate DESC
			""")
	Optional<ContractOffer> findValidContractOffer(@Param("qualityId") Integer qualityId,
			@Param("sellerId") Integer sellerId, @Param("buyerId") Integer buyerId,
			@Param("status") String status);

	/**
	 * Recherche une liste de contrats selon l'ID de la qualité, du vendeur et de l'acheteur.
	 *
	 * @param qualityId
	 *            ID de la qualité.
	 * @param sellerId
	 *            ID du vendeur.
	 * @param buyerId
	 *            ID de l'acheteur.
	 * @return Une List contenant les offres de contrat correspondantes.
	 */
	List<ContractOffer> findByQualityIdAndSellerIdAndBuyerId(Integer qualityId, Integer sellerId,
			Integer buyerId);

	@Modifying
	@Transactional
	@Query(value = """
			  	UPDATE contract_offer
			    SET creation_date = :newDate
			   	WHERE id = :id
			""", nativeQuery = true)
	void overrideCreationDateNative(@Param("id") Integer id,
			@Param("newDate") LocalDateTime newDate);
}
