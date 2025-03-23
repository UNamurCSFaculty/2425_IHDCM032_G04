package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "field", indexes = {@Index(name = "idx_field_producer", columnList = "id_producer")})
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un champ agricole. */
public class Field {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "field_seq")
	@SequenceGenerator(name = "field_seq", sequenceName = "field_seq", allocationSize = 1)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_producer", nullable = false)
	@ToString.Exclude
	private Producer producer;

	@Column(name = "location", nullable = false)
	private String location;

	@Column(name = "surface_area_m2", nullable = false)
	private BigDecimal surfaceAreaM2;

	@Column(name = "details", columnDefinition = "TEXT")
	private String details;
}
