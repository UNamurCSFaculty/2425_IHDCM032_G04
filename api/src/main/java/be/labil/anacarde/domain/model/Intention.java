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

@Entity
@Table(name = "intention")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Intention extends BaseEntity {

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime date;

	@Column(nullable = false)
	private Integer quantity;

	@ManyToOne(optional = false)
	@JoinColumn(name = "quality_id", nullable = false)
	private Quality quality;

	@ManyToOne(optional = false)
	@JoinColumn(name = "buyer_id", nullable = false)
	private Trader buyer;
}