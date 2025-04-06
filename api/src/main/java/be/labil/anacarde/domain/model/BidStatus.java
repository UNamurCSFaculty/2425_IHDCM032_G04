package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bid_status")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BidStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_offer_status")
	private Integer id;

	@Column(nullable = false)
	private String name;
}
