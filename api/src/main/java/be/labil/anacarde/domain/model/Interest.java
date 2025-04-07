package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "interest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Interest {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_interest")
	private Integer id;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private LocalDateTime date;

	@ManyToOne(optional = false)
	private Intention intention;

	@ManyToOne(optional = false)
	private Trader buyer;
}
