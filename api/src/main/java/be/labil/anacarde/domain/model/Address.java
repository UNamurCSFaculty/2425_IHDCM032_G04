package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
	@Column(name = "street", nullable = false)
	private String street;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", updatable = false)
	private City city;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id", updatable = false)
	private Region region;
}
