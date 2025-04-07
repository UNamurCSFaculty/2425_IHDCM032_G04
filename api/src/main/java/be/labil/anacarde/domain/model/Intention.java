package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "intention")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Intention {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_intention")
	private Integer id;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private LocalDateTime date;

	@Column(nullable = false)
	private Integer quantity;

	@ManyToOne(optional = false)
	private Quality quality;

	@ManyToOne(optional = false)
	private Trader buyer;
}