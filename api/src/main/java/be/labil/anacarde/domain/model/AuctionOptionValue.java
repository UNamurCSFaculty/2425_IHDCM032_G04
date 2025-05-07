package be.labil.anacarde.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auction_option_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionOptionValue extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "auction_option_id", nullable = false)
	private AuctionOption auctionOption;

	private String optionValue;
}
