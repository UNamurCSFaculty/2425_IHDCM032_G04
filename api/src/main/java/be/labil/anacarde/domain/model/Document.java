package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "document", indexes = {@Index(columnList = "user_id")})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un document dans le système. */
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_seq")
	@SequenceGenerator(name = "document_seq", sequenceName = "document_seq", allocationSize = 1)
	private Integer id;

	@Column(nullable = false)
	private String documentType;

	@Column(nullable = false)
	private String format;

	@Column(nullable = false)
	private String storagePath;

	@Column(nullable = false)
	private LocalDateTime uploadDate = LocalDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@ToString.Exclude
	private User user;

	/**
	 * La comparaison est basée sur l'identifiant unique du document. Cette méthode gère correctement les instances
	 * proxy de Hibernate.
	 *
	 * @param o
	 *            L'objet à comparer avec ce document.
	 * @return true si l'objet spécifié est égal à ce document ; sinon, false.
	 */
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Document document = (Document) o;
		return getId() != null && Objects.equals(getId(), document.getId());
	}

	/**
	 * Le hash code est dérivé du type de la classe afin de gérer correctement les instances proxy de Hibernate.
	 *
	 * @return Le hash code en tant qu'entier.
	 */
	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}
