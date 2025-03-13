package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(
        name = "document",
        indexes = {@Index(columnList = "user_id")})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/** Entity representing a Document in the system. */
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

    @Override
    /**
     * The comparison is based on the unique identifier of the document. This method properly
     * handles Hibernate proxy instances.
     *
     * @param o The object to compare with this Document.
     * @return True if the specified object is equal to this Document; otherwise, false.
     */
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass =
                o instanceof HibernateProxy
                        ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                        : o.getClass();
        Class<?> thisEffectiveClass =
                this instanceof HibernateProxy
                        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                        : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Document document = (Document) o;
        return getId() != null && Objects.equals(getId(), document.getId());
    }

    @Override
    /**
     * The hash code is derived from the class type to properly account for Hibernate proxy
     * instances.
     *
     * @return The hash code as an integer.
     */
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this)
                        .getHibernateLazyInitializer()
                        .getPersistentClass()
                        .hashCode()
                : getClass().hashCode();
    }
}
