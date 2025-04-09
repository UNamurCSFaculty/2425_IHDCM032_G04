package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "translation_entry", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"translation_id", "language_id"})})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TranslationEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "translation_entry_seq")
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "translation_id", nullable = false)
	private Translation translation;

	@ManyToOne(optional = false)
	@JoinColumn(name = "language_id", nullable = false)
	private Language language;

	@Column(name = "translated_text", nullable = false)
	private String text;
}