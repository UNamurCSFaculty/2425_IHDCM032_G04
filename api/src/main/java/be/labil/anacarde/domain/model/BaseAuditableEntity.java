package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
/** Abstract base class for auditable entities. */
public abstract class BaseAuditableEntity implements Serializable {

    /** Timestamp indicating when the entity was created. */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdDate;

    /** Timestamp indicating when the entity was last modified. */
    @LastModifiedDate protected LocalDateTime lastModifiedDate;

    // @Version
    // private int version;
    // @LastModifiedBy
    // protected String lastModifiedBy;
    // @CreatedBy
    // protected String createdBy;
}
