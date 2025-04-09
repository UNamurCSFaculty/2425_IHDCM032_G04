package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "region")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Region {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_region")
	private Integer id;

	@Column(nullable = false)
	private String name;
}