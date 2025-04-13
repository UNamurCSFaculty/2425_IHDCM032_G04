package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "auction_strategy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuctionStrategy {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_strategy")
	private Integer id;

	@Column(nullable = false)
	private String name;
}