package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entité représentant une marque d'intérêt emit par un Trader pour une Intention de vente.
 */
@Entity
@Table(name = "interest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Interest extends BaseEntity {

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime date;

	@ManyToOne(optional = false)
	@JoinColumn(name = "intention_id", nullable = false)
	private Intention intention;

	@ManyToOne(optional = false)
	@JoinColumn(name = "buyer_id", nullable = false)
	private Trader buyer;
}
