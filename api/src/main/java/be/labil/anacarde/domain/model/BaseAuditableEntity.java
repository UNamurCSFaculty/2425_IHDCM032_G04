package be.labil.anacarde.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
/**
 * Classe de base abstraite pour les entités auditées. Fournit la gestion automatique des dates de création et de
 * dernière modification de l'entité.
 */
public abstract class BaseAuditableEntity implements Serializable {

	/** Date et heure de création de l'entité. */
	@CreatedDate
	@Column(nullable = false, updatable = false)
	protected LocalDateTime createdDate;

	/** Date et heure de la dernière modification de l'entité. */
	@LastModifiedDate
	protected LocalDateTime lastModifiedDate;
}

// @Version
// private int version;
// @LastModifiedBy
// protected String lastModifiedBy;
// @CreatedBy
// protected String createdBy;
