package be.labil.anacarde.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "quality_document")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un document dans le système. */
public class QualityDocument extends Document {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quality_certification_id", nullable = false)
	@ToString.Exclude
	private QualityCertification qualityCertification;

}
