package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "translation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Translation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "translation_seq")
	private Integer id;

	@Column(name = "translation_key", nullable = false, unique = true)
	private String key;
}