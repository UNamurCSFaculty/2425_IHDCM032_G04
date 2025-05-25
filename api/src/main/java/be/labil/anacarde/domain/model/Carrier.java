package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
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
	private Double pricePerKm;

	@Column(nullable = false)
	private Double radius;
}