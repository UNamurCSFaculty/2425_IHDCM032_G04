package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "auction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Auction {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_auction")
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
	private Boolean active;

	@ManyToOne(optional = false)
	private AuctionStrategy strategy;

	@ManyToOne(optional = false)
	private Product product;

	@OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<AuctionOptionValue> auctionOptionValues = new HashSet<>();
}
