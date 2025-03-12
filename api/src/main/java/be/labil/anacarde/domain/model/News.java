package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "news")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/**
 * @brief Entity representing a News item in the system.
 */
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_seq")
    @SequenceGenerator(name = "news_seq", sequenceName = "news_seq", allocationSize = 1)
    private Integer id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime publicationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    @Override
    /**
     * @brief Compares this News object with the specified object for equality.
     *
     * The comparison is based on the unique identifier of the news item. It correctly handles Hibernate proxy instances.
     *
     * @param o The object to compare with this News instance.
     * @return True if the specified object is equal to this News; otherwise, false.
     */
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        News news = (News) o;
        return getId() != null && Objects.equals(getId(), news.getId());
    }

    @Override
    /**
     * @brief Returns the hash code value for this News item.
     *
     * The hash code is derived from the class type to properly account for Hibernate proxy instances.
     *
     * @return The hash code as an integer.
     */
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
