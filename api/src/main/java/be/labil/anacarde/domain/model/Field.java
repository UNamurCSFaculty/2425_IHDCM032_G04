package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
	private Producer producer;
}