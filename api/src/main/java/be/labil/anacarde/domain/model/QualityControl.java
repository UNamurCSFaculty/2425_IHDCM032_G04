package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quality_control")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityControl {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_quality_control")
	private Integer id;

	@Column(nullable = false)
	private String identifier;

	@Column(nullable = false)
	private LocalDateTime controlDate;

	@Column(nullable = false)
	private Float granularity;

	@Column(nullable = false)
	private Float korTest;

	@Column(nullable = false)
	private Float humidity;

	@ManyToOne(optional = false)
	private QualityInspector qualityInspector;

	@ManyToOne(optional = false)
	private Product product;

	@ManyToOne(optional = false)
	private Quality quality;

	@ManyToOne(optional = false)
	private Document document;
}
