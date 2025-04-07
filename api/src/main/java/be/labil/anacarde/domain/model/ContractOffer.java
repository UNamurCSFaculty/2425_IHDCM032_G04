package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "contract_offer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractOffer {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_contract_offer")
	private Integer id;

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	private BigDecimal pricePerKg;

	@Column(nullable = false)
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private Float duration;

	@ManyToOne(optional = false)
	private Trader seller;

	@ManyToOne(optional = false)
	private Trader buyer;

	@ManyToOne(optional = false)
	private Quality quality;
}