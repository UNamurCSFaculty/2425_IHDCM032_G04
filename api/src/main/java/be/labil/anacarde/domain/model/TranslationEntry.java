package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité représentant la traduction d'une clé dans une langue donnée.
 */
@Entity
@Table(name = "translation_entry", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"translation_id", "language_id"})})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TranslationEntry extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "translation_id", nullable = false)
	private Translation translation;

	@ManyToOne(optional = false)
	@JoinColumn(name = "language_id", nullable = false)
	private Language language;

	@Column(name = "translated_text", nullable = false)
	private String text;
}