package be.labil.anacarde.infrastructure.persistence;

import be.labil.anacarde.domain.model.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository Spring Data JPA pour l’entité {@link TradeStatus}.
 * <p>
 * Fournit l’accès en lecture aux différents états possibles d’une transaction
 * (accepté, refusé, ouvert, expiré), identifiés par leur nom.
 */
public interface TradeStatusRepository extends JpaRepository<TradeStatus, Integer> {

	/**
	 * Retourne le status accepté.
	 *
	 * @return Un status correspondant à l'acceptation d'un achat/vente.
	 */
	@Query("SELECT s FROM TradeStatus s WHERE s.name = 'Accepté'")
	TradeStatus findStatusAccepted();

	/**
	 * Retourne le status refusé.
	 *
	 * @return Un status correspondant au refus d'un achat/vente.
	 */
	@Query("SELECT s FROM TradeStatus s WHERE s.name = 'Refusé'")
	TradeStatus findStatusRejected();

	/**
	 * Retourne le status en cours.
	 *
	 * @return Un status correspondant à l'évaluation d'une achat/vente.
	 */
	@Query("SELECT s FROM TradeStatus s WHERE s.name = 'Ouvert'")
	TradeStatus findStatusPending();

	/**
	 * Retourne le status en cours.
	 *
	 * @return Un status correspondant à l'évaluation d'une achat/vente.
	 */
	@Query("SELECT s FROM TradeStatus s WHERE s.name = 'Expiré'")
	TradeStatus findStatusExpired();
}
