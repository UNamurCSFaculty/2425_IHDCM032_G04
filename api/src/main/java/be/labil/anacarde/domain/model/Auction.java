package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "auction")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Auction {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sale_offer")
	private Integer id;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private Integer productQuantity;

	@Column(nullable = false)
	private LocalDateTime expirationDate;

	@Column(nullable = false)
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private String active;

	@ManyToOne(optional = false)
	private AuctionStrategy strategy;

	@ManyToOne(optional = false)
	private Product product;

	@OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<AuctionOptionValue> auctionOptionValues = new HashSet<>();

	/*
	 * @ManyToMany
	 * 
	 * @JoinTable( name = "auction_auction_option", joinColumns = @JoinColumn(name = "auction_id"), inverseJoinColumns
	 * = @JoinColumn(name = "auction_option_id") ) private Set<AuctionOption> auctionOptions = new HashSet<>();
	 * 
	 */
}
