package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

/**
 * Entité représentant une adresse complête.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
	@Column(name = "street")
	private String street;

	@JdbcTypeCode(SqlTypes.GEOMETRY)
	private Point location;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id")
	private City city;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id")
	private Region region;
}
