package be.labil.anacarde.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "producer")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Producer extends Trader {

	@Column(nullable = false)
	private String agriculturalIdentifier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cooperative_id")
	@JsonBackReference("coop-producers")
	private Cooperative cooperative;
}
