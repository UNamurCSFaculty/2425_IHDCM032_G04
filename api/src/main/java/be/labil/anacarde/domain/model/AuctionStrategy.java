package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auction_strategy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionStrategy {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_strategy")
	private Integer id;

	@Column(nullable = false)
	private String name;
}