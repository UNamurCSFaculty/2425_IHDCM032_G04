package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "region")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_region")
	private Integer id;

	@Column(nullable = false)
	private String name;
}