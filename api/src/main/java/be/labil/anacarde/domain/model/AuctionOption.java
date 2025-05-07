package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auction_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionOption extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String name;
}
