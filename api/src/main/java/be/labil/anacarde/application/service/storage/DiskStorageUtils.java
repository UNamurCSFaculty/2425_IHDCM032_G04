package be.labil.anacarde.application.service.storage;

import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.domain.model.User;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

/**
 * Utilitaires pour la gestion des documents stockés sur disque. Fournit des méthodes pour
 * construire des objets Document à partir de fichiers et d'utilisateurs.
 */
public class DiskStorageUtils {

	public static Document buildDocument(User user, MultipartFile f, String path) {
		String ext = getExt(Objects.requireNonNull(f.getOriginalFilename()));
		return Document.builder().user(user).originalFilename(f.getOriginalFilename())
				.size(f.getSize()).contentType(f.getContentType()).extension(ext).storagePath(path)
				.uploadDate(LocalDateTime.now()).build();
	}

	public static Document buildDocument(QualityControl qualityControl, MultipartFile f,
			String path) {
		String ext = getExt(Objects.requireNonNull(f.getOriginalFilename()));
		return Document.builder().qualityControl(qualityControl)
				.originalFilename(f.getOriginalFilename()).size(f.getSize())
				.contentType(f.getContentType()).extension(ext).storagePath(path)
				.uploadDate(LocalDateTime.now()).build();

	}
	public static String getExt(String name) {
		int i = name.lastIndexOf('.');
		return i > 0 ? name.substring(i + 1).toLowerCase() : "";
	}
}
