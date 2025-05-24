package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "quality_control")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QualityControl extends BaseEntity {

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

	// TODO EAGER
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "qualityInspector_id", nullable = false)
	private QualityInspector qualityInspector;

	@ManyToOne(optional = false)
	@JoinColumn(name = "quality_id", nullable = false)
	private Quality quality;

	// TODO set doc id mandatory
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id", nullable = true)
	private Document document;
}
