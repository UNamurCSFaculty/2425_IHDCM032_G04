package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Paramètres globaux applicables par défaut aux enchères
 */
@Entity
@Table(name = "global_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GlobalSettings {

	// On force id à toujours valoir 1
	@Id
	@Column(name = "id", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 1 CHECK (id = 1)")
	@Builder.Default
	private Integer id = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "default_strategy_id")
	private AuctionStrategy defaultStrategy;

	@Column(name = "default_fixed_price_kg")
	private Double defaultFixedPriceKg;

	@Column(name = "default_max_price_kg")
	private Double defaultMaxPriceKg;

	@Column(name = "default_min_price_kg")
	private Double defaultMinPriceKg;

	@Column(name = "show_only_active", nullable = false)
	@Builder.Default
	private Boolean showOnlyActive = Boolean.TRUE;
}