package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "field", indexes = {@Index(name = "idx_field_producer", columnList = "id_producer")})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/** Entité représentant un champ agricole. */
public class Field {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "field_seq")
	@SequenceGenerator(name = "field_seq", sequenceName = "field_seq", allocationSize = 1)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_producer", nullable = false)
	@ToString.Exclude
	private Producer producer;

	@Column(name = "location", nullable = false)
	private String location;

	@Column(name = "surface_area_m2", nullable = false)
	private Double surfaceAreaM2;

	@Column(name = "details", columnDefinition = "TEXT")
	private String details;

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
		Field field = (Field) o;
		return getId() != null && Objects.equals(getId(), field.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}
