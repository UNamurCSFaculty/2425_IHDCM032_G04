package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carrier")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un transporteur dans le système. */
public class Carrier {

	@Id
	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@MapsId
	private User user;

	@Column(name = "gps_location", nullable = false)
	private String gpsLocation;

	@Column(name = "km_range", nullable = false)
	private Double kmRange;

	@Column(name = "km_price", nullable = false)
	private Double kmPrice;

}
