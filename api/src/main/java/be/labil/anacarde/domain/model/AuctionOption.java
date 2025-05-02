package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
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
