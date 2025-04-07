package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auction_option_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionOptionValue {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_auction_option_value")
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "auction_id", nullable = false)
	private Auction auction;

	@ManyToOne(optional = false)
	@JoinColumn(name = "auction_option_id", nullable = false)
	private AuctionOption auctionOption;

	@Column(nullable = false)
	private String value;
}
