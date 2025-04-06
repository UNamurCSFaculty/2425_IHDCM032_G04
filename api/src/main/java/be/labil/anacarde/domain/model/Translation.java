package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "translation")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Translation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "translation_seq")
	private Integer id;

	@Column(name = "translation_key", nullable = false, unique = true)
	private String key;
}