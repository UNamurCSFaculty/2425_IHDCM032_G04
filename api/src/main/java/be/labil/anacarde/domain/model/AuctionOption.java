package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auction_option", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionOption {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_auction_option")
	private Integer id;

	@Column(nullable = false)
	private String name;
}
