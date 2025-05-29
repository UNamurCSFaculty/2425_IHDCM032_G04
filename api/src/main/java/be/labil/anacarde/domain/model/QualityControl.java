package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
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

	@OneToMany(mappedBy = "qualityControl", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Builder.Default
	private List<Document> documents = new ArrayList<>();
}
