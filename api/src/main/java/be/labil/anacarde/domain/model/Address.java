package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
	@Column(name = "street", nullable = false)
	private String street;

	@JdbcTypeCode(SqlTypes.GEOMETRY)
	private Point location;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", updatable = false)
	private City city;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id", updatable = false)
	private Region region;
}
