package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "bid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_purchase_offer")
	private Integer id;

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false)
	private LocalDateTime auctionDate;

	@Column(nullable = false)
	private LocalDateTime creationDate;

	@ManyToOne(optional = false)
	private Auction auction;

	@ManyToOne(optional = false)
	private Trader trader;

	@ManyToOne(optional = false)
	private BidStatus status;
}
