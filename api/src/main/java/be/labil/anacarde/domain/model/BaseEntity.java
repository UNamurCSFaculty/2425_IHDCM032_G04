package be.labil.anacarde.domain.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public abstract class BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	/**
	 * La comparaison est basée sur l'identifiant unique de l'entité. Une gestion particulière est prévue pour comparer
	 * correctement les instances proxy gérées par Hibernate.
	 *
	 * @param o
	 *            L'objet à comparer.
	 * @return true si l'objet donné représente le même objet ; sinon, false.
	 */
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;

		Class<?> thisClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		Class<?> otherClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		if (!Objects.equals(thisClass, otherClass)) return false;

		BaseEntity other = (BaseEntity) o;
		return id != null && Objects.equals(id, other.id);
	}

	/**
	 * Le hash code est basé sur le type de la classe, en tenant compte des instances proxy de Hibernate.
	 *
	 * @return Le hash code en tant qu'entier.
	 */
	@Override
	public final int hashCode() {
		Class<?> thisClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		return thisClass.hashCode();
	}
}