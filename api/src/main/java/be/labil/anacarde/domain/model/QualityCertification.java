package be.labil.anacarde.domain.model;

import be.labil.anacarde.domain.enums.Quality;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

/**
 * Entité représentant une certification de qualité dans le système.
 */
@Entity
@Table(name = "quality_certification")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QualityCertification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	@ToString.Exclude
	private Store store;

	@Column(nullable = false)
	private LocalDateTime date;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Quality quality;

	@Column(nullable = false)
	private BigDecimal weight;

	@Column(nullable = false)
	private BigDecimal seeding;

	@Column(nullable = false)
	private BigDecimal kor;

	@Column(name = "humidity_rate", nullable = false)
	private BigDecimal humidityRate;

	@Column(name = "defect_rate", nullable = false)
	private BigDecimal defectRate;

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
		QualityCertification that = (QualityCertification) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}
