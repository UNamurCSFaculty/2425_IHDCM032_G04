package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.ContractOffer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
