package be.labil.anacarde.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public abstract class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
	private Integer id;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime deliveryDate;

	@Column(nullable = false)
	private Double weightKg;

	@OneToOne(mappedBy = "product")
	@JoinColumn(name = "qualityControl_id")
	@JsonManagedReference
	private QualityControl qualityControl;
}
