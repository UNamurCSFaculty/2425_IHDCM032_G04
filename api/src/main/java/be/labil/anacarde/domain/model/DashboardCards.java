package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import org.hibernate.annotations.Immutable;

/**
 * Entité représentant les données agrégées affichées sur le tableau de bord amdin. ENTITÉ
 * STRICTEMENT EN LECTURE : aucune opération INSERT/UPDATE/DELETE. La vue ne contient qu’une seule
 * ligne ; on désigne {@code total_nb_users} comme clé primaire technique.
 */
@Getter
@Entity
@Immutable
public class DashboardCards {

	/* ========== USERS ========== */
	@Id
	@Column(name = "total_nb_users")
	private Long totalNbUsers;

	@Column(name = "total_nb_users_tendency")
	private Double totalNbUsersTendency;

	@Column(name = "pending_validation")
	private Long pendingValidation;

	@Column(name = "pending_validation_tendency")
	private Double pendingValidationTendency;

	/* ========== AUCTIONS ========== */
	@Column(name = "total_auctions")
	private Long totalAuctions;

	@Column(name = "total_auctions_tendency")
	private Double totalAuctionsTendency;

	@Column(name = "auctions_concluded")
	private Long auctionsConcluded;

	@Column(name = "auctions_concluded_tendency")
	private Double auctionsConcludedTendency;

	/* ========== LOT / SALES ========== */
	@Column(name = "total_lot_weight_kg")
	private Double totalLotWeightKg;

	@Column(name = "total_lot_weight_kg_tendency")
	private Double totalLotWeightKgTendency;

	@Column(name = "total_sold_weight_kg")
	private Double totalSoldWeightKg;

	@Column(name = "total_sold_weight_kg_tendency")
	private Double totalSoldWeightKgTendency;

	@Column(name = "total_sales_amount")
	private BigDecimal totalSalesAmount;

	@Column(name = "total_sales_amount_tendency")
	private Double totalSalesAmountTendency;

	@Column(name = "monthly_sales_amount")
	private Double monthlySalesAmount;

	@Column(name = "monthly_sales_amount_tendency")
	private Double monthlySalesAmountTendency;

	@Column(name = "grade1_price")
	private Double grade1Price;

	@Column(name = "grade1_price_tendency")
	private Double grade1PriceTendency;

	@Column(name = "grade2_price")
	private Double grade2Price;

	@Column(name = "grade2_price_tendency")
	private Double grade2PriceTendency;

	@Column(name = "grade3_price")
	private Double grade3Price;

	@Column(name = "grade3_price_tendency")
	private Double grade3PriceTendency;

	@Column(name = "hors_category_price")
	private Double horsCategoryPrice;

	@Column(name = "hors_category_tendency")
	private Double horsCategoryTendency;

	@Column(name = "transformed_price")
	private Double transformedPrice;

	@Column(name = "transformed_price_tendency")
	private Double transformedPriceTendency;
}
