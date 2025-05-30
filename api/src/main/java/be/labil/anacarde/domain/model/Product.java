package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "product")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Product extends BaseEntity {

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime deliveryDate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(nullable = false)
	private Double weightKg;

	@Column(nullable = false)
	private Double weightKgAvailable;

	@OneToOne(optional = true)
	@JoinColumn(name = "qualityControl_id", nullable = true)
	private QualityControl qualityControl;

	@PrePersist
	public void prePersist() {
		if (weightKgAvailable == null || weightKgAvailable > weightKg) {
			weightKgAvailable = weightKg;
		}
	}
}
