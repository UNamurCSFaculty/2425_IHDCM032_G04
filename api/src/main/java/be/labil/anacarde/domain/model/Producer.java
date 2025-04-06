package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "producer")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Producer extends Trader {

	@Column(nullable = false)
	private String agriculturalIdentifier;

	@ManyToOne
	private Cooperative cooperative;
}
