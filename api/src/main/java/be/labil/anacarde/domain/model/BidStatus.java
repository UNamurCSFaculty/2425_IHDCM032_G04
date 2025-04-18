package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "bid_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BidStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_offer_status")
	private Integer id;

	@Column(nullable = false)
	private String name;
}
