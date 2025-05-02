package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "carrier")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Carrier extends User {

	@Column(nullable = false)
	private BigDecimal pricePerKm;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "carrier_region", joinColumns = @JoinColumn(name = "carrier_id"), inverseJoinColumns = @JoinColumn(name = "region_id"))
	private Set<Region> regions;
}