package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "field")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Field {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_field")
	private Integer id;

	@Column(nullable = false)
	private String identifier;

	@JdbcTypeCode(SqlTypes.GEOMETRY)
	private Point location;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producer_id", nullable = false)
	private Producer producer;
}