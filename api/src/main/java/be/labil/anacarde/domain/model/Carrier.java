package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "carrier")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un transporteur dans le système. */
public class Carrier {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrier_seq")
	@SequenceGenerator(name = "carrier_seq", sequenceName = "carrier_seq", allocationSize = 1)
	private Integer id;

	@Column(name = "gps_location", nullable = false)
	private String gpsLocation;

	@Column(name = "km_range", nullable = false)
	private BigDecimal kmRange;

	@Column(name = "km_price", nullable = false)
	private BigDecimal kmPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_user", nullable = false)
	@ToString.Exclude
	private User user;

}
