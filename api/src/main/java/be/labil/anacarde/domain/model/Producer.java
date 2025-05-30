package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entité représentant un producteur et héritant de Trader.
 */
@Entity
@Table(name = "producer")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Producer extends Trader {

	@Column(nullable = false, unique = true)
	private String agriculturalIdentifier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cooperative_id")
	private Cooperative cooperative;
}
