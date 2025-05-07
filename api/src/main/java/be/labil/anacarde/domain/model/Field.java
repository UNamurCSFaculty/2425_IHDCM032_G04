package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "field")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Field extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String identifier;

	@JdbcTypeCode(SqlTypes.GEOMETRY)
	private Point location;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producer_id", nullable = false)
	private Producer producer;
}