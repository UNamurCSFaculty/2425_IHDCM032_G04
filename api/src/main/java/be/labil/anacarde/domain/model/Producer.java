package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producer")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un producteur dans le système. */
public class Producer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producer_seq")
	@SequenceGenerator(name = "producer_seq", sequenceName = "producer_seq", allocationSize = 1)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cooperative")
	@ToString.Exclude
	private Cooperative cooperative;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_bidder", nullable = false)
	@ToString.Exclude
	private Bidder bidder;
}
