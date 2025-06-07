package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.Bid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository Spring Data pour l’entité {@link Bid}.
 * <p>
 * Fournit les opérations CRUD de base et des méthodes de recherche et de mise à jour
 * spécifiques aux offres d’enchères.
 */
public interface BidRepository extends JpaRepository<Bid, Integer> {
	/**
	 * Recherche les offres correspondant à une enchère donnée.
	 *
	 * @param auctionId
	 *            L'ID de l'enchère correspondante.
	 * @return Une liste d'offres correspondant à l'enchère.
	 */
	List<Bid> findByAuctionId(Integer auctionId);

	/**
	 * Recherche les offres correspondant à une enchère donnée, par ordre de création (id).
	 *
	 * @param auctionId
	 *            L'ID de l'enchère correspondante.
	 * @return Une liste d'offres correspondant à l'enchère.
	 */
	List<Bid> findByAuctionIdOrderByIdAsc(Integer auctionId);

	/**
	 * Met à jour nativement la date de création ({@code creation_date})
	 * d’une offre identifiée par son ID.
	 * <p>
	 * Utilise une requête SQL native pour modifier directement la colonne.
	 *
	 * @param id      identifiant de l’offre à mettre à jour
	 * @param newDate nouvelle date de création à appliquer
	 */
	@Modifying
	@Transactional
	@Query(value = """
			  	UPDATE bid
			    SET creation_date = :newDate
			   	WHERE id = :id
			""", nativeQuery = true)
	void overrideCreationDateNative(@Param("id") Integer id,
			@Param("newDate") LocalDateTime newDate);

	/**
	 * Vérifie si une offre existe et appartient à un trader donné.
	 *
	 * @param auctionId identifiant de l’offre (enchère)
	 * @param userId    identifiant du trader ayant placé l’offre
	 * @return {@code true} si l’offre existe et que son trader correspond, {@code false} sinon
	 */
	boolean existsByIdAndTraderId(Integer auctionId, Integer userId);
}
