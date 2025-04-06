package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "role", indexes = {@Index(name = "idx_role_name", columnList = "name")})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
/** Entité représentant un rôle dans le système. */
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
	private Integer id;

	@Column(nullable = false, unique = true)
	private String name;

	@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
	@ToString.Exclude
	private Set<User> users;

	/**
	 * La comparaison se base sur l'identifiant unique du rôle. Si les objets sont de classes effectives différentes,
	 * ils sont considérés comme non égaux. Cette méthode gère correctement les instances proxy de Hibernate.
	 *
	 * @param o
	 *            L'objet à comparer avec ce rôle.
	 * @return true si l'objet spécifié est égal à ce rôle ; sinon, false.
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
		Role role = (Role) o;
		return getId() != null && Objects.equals(getId(), role.getId());
	}

	/**
	 * Le hash code est basé sur le type de classe pour prendre en compte correctement les instances proxy de Hibernate.
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
