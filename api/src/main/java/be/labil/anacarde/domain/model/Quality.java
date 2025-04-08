package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quality")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quality {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_quality")
	private Integer id;

	@Column(nullable = false)
	private String name;
}