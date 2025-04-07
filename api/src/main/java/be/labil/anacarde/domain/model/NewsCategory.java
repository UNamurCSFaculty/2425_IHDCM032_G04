package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "category", indexes = {@Index(name = "idx_category_name", columnList = "name")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant une catégorie dans le système. */
public class NewsCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
	private Integer id;

	@Column(nullable = false, unique = true)
	private String name;

	/**
	 * La comparaison est basée sur l'identifiant unique de la catégorie. Elle gère correctement les instances proxy de
	 * Hibernate.
	 *
	 * @param o
	 *            L'objet à comparer avec cette catégorie.
	 * @return true si l'objet spécifié est égal à cette catégorie ; sinon, false.
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
		NewsCategory newsCategory = (NewsCategory) o;
		return getId() != null && Objects.equals(getId(), newsCategory.getId());
	}

	/**
	 * Le hash code est dérivé du type de classe pour prendre correctement en compte les instances proxy de Hibernate.
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
