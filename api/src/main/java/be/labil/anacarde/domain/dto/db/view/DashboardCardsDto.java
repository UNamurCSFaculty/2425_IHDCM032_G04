package be.labil.anacarde.domain.dto.db.view;

import java.math.BigDecimal;
import lombok.*;

/**
 * Ligne unique de la vue v_dashboard_stats. Immuable. Contient les indicateurs globaux du dashboard
 * administrateur.
 */
@Getter
@Builder
@AllArgsConstructor
public class DashboardCardsDto {

	/** Nombre total d'utilisateurs */
	private Long totalNbUsers;

	/**
	 * Pourcentage d'augmentation/diminution du nombre total d'utilisateurs par rapport à il y a 30
	 * jours
	 */
	private Double totalNbUsersTendency;

	/** Nombre de validations en attente */
	private Long pendingValidation;

	/**
	 * Pourcentage d'augmentation/diminution du nombre de validations en attente par rapport à il y
	 * a 30 jours
	 */
	private Double pendingValidationTendency;

	/** Nombre total d’enchères lancées */
	private Long totalAuctions;

	/**
	 * Pourcentage d'augmentation/diminution du nombre total d'enchères par rapport à il y a 30
	 * jours
	 */
	private Double totalAuctionsTendency;

	/** Nombre d’enchères conclues (avec gagnant) sur les 30 jours précédents */
	private Long auctionsConcluded;

	/**
	 * Pourcentage d'augmentation/diminution du nombre d’enchères conclues sur les 30 jours
	 * précédents par rapport au 30 d'avant.
	 */
	private Double auctionsConcludedTendency;

	/** Poids total mis en vente (tous lots, en kg) */
	private Double totalLotWeightKg;

	/**
	 * Pourcentage d'augmentation/diminution du poids total mis en vente par rapport à il y a 30
	 * jours
	 */
	private Double totalLotWeightKgTendency;

	/** Poids total vendu (tous lots conclus, en kg) les 30 jours précédents */
	private Double totalSoldWeightKg;

	/**
	 * Pourcentage d'augmentation/diminution du poids total vendu sur les 30 jours précédents par
	 * rapport au 30 d'avant.
	 */
	private Double totalSoldWeightKgTendency;

	/** Montant total des ventes (somme des offres gagnantes) sur les 30 derniers jours */
	private BigDecimal totalSalesAmount;

	/**
	 * Pourcentage d'augmentation/diminution du montant total des ventes sur les 30 jours précédents
	 * par rapport au 30 d'avant.
	 */
	private Double totalSalesAmountTendency;
}
