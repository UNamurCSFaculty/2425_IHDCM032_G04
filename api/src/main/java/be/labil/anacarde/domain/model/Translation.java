package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité représentant une clé d'un litéral pouvant être retranscrit dans une langue donnée
 */
@Entity
@Table(name = "translation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Translation extends BaseEntity {

	@Column(name = "translation_key", nullable = false, unique = true)
	private String key;
}