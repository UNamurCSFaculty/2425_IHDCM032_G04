package be.labil.anacarde.infrastructure.persistence.view;

import be.labil.anacarde.domain.model.ExportAuction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository en lecture seule pour la vue SQL <code>v_auction_bid_analysis</code>.
 * <p>
 * Expose des méthodes natives pour récupérer les données agrégées des enchères
 * sous forme d’instances de {@link ExportAuction}.
 */
public interface ExportAuctionRepository extends JpaRepository<ExportAuction, Integer> {

	/**
	 * Récupère toutes les lignes de la vue sans aucun filtre.
	 *
	 * @return liste complète de {@link ExportAuction} issues de la vue
	 */
	@Query(value = "SELECT * FROM v_auction_bid_analysis", nativeQuery = true)
	List<ExportAuction> findAllView();

	/**
	 * Recherche une entrée de la vue correspondant à une enchère précise.
	 *
	 * @param id identifiant de l’enchère à rechercher
	 * @return un {@link Optional} contenant l’instance {@link ExportAuction} si trouvée,
	 *         sinon vide
	 */
	@Query(value = """
			SELECT *
			FROM   v_auction_bid_analysis
			WHERE  auction_id = :id
			""", nativeQuery = true)
	Optional<ExportAuction> findByAuctionId(@Param("id") Integer id);

	/**
	 * Récupère les enchères dont la date de début est comprise entre deux bornes.
	 * <p>
	 * Si {@code onlyEnded} est à {@code true}, ne renvoie que celles dont
	 * le flag {@code auction_ended} est à {@code true}.
	 *
	 * @param start     date de début minimale (inclusive)
	 * @param end       date de début maximale (inclusive)
	 * @param onlyEnded indicateur pour ne renvoyer que les enchères terminées
	 * @return liste des {@link ExportAuction} correspondant aux critères
	 */
	@Query(value = """
			SELECT *
			FROM   v_auction_bid_analysis
			WHERE  auction_start_date BETWEEN :start AND :end
			  AND  (:onlyEnded = false OR auction_ended = true)
			""", nativeQuery = true)
	List<ExportAuction> findAllByStartDateBetween(@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end, @Param("onlyEnded") boolean onlyEnded);
}
