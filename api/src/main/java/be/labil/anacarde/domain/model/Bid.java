package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "bid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Bid {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_purchase_offer")
	private Integer id;

	@Column(nullable = false)
	private BigDecimal amount;

	// TODO zak qu'est-ce que ça fait là ?
	@Column(nullable = false)
	private LocalDateTime auctionDate;

	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime creationDate;

	@ManyToOne(optional = false)
	private Auction auction;

	@ManyToOne(optional = false)
	private Trader trader;

	@ManyToOne(optional = false)
	private BidStatus status;
}
