package be.labil.anacarde.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "document", indexes = @Index(columnList = "user_id"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Document extends BaseEntity {

	/** MIME type, ex. : `application/pdf` */
	@Column(nullable = false)
	private String contentType;

	/** Nom original du fichier pour l’affichage */
	@Column(nullable = false)
	private String originalFilename;

	/** Taille du fichier en octets */
	@Column(nullable = false)
	private long size;

	/** Extension (pdf, jpg, …) – pratique pour filtrer rapidement */
	@Column(nullable = false, length = 10)
	private String extension;

	/** Emplacement final (chemin disque ou clé S3) */
	@Column(nullable = false)
	private String storagePath;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime uploadDate;

	/* relations */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = true)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quality_control", nullable = true)
	private QualityControl qualityControl;
}
