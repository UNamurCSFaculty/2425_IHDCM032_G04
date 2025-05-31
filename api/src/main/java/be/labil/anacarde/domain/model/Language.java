package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entité représentant une langue disponible pour l'application.
 */
@Entity
@Table(name = "language")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Language extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false, unique = true)
	private String code;
}