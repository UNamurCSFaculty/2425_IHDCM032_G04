package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un article de presse dans le système. */
public class News {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_seq")
	private Integer id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private LocalDateTime creationDate = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime publicationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private NewsCategory category;

	/**
	 * La comparaison se base sur l'identifiant unique de l'article. Cette méthode gère correctement les instances proxy
	 * de Hibernate.
	 *
	 * @param o
	 *            L'objet à comparer avec cet article.
	 * @return true si l'objet spécifié est égal à cet article ; sinon, false.
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
		News news = (News) o;
		return getId() != null && Objects.equals(getId(), news.getId());
	}

	/**
	 * Le hash code est dérivé du type de la classe afin de prendre correctement en compte les instances proxy de
	 * Hibernate.
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
