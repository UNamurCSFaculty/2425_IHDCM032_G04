package be.labil.anacarde.application.service.storage;

import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.domain.model.User;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

/**
 * Fournit des utilitaires pour la création d'objets {@link Document} à partir de fichiers uploadés
 * sur disque. Propose deux méthodes de construction selon que le document soit lié à un
 * {@link User} ou à un {@link QualityControl}.
 */
public class DiskStorageUtils {

	/**
	 * Construit un {@link Document} à partir d'un fichier uploadé par un utilisateur.
	 *
	 * @param user
	 *            l'utilisateur ayant uploadé le fichier
	 * @param f
	 *            le fichier multipart contenant le document
	 * @param path
	 *            le chemin de stockage du fichier sur le disque
	 * @return un objet {@link Document} initialisé (nom d'origine, taille, type, extension, chemin
	 *         de stockage et date d'upload)
	 * @throws NullPointerException
	 *             si le nom original du fichier est null
	 */
	public static Document buildDocument(User user, MultipartFile f, String path) {
		String ext = getExt(Objects.requireNonNull(f.getOriginalFilename()));
		return Document.builder().user(user).originalFilename(f.getOriginalFilename())
				.size(f.getSize()).contentType(f.getContentType()).extension(ext).storagePath(path)
				.uploadDate(LocalDateTime.now()).build();
	}

	/**
	 * Construit un {@link Document} à partir d'un fichier uploadé lors d'un contrôle qualité.
	 *
	 * @param qualityControl
	 *            l'entité {@link QualityControl} associée au document
	 * @param f
	 *            le fichier multipart contenant le document
	 * @param path
	 *            le chemin de stockage du fichier sur le disque
	 * @return un objet {@link Document} initialisé (entité QC, nom d'origine, taille, type,
	 *         extension, chemin de stockage et date d'upload)
	 * @throws NullPointerException
	 *             si le nom original du fichier est null
	 */
	public static Document buildDocument(QualityControl qualityControl, MultipartFile f,
			String path) {
		String ext = getExt(Objects.requireNonNull(f.getOriginalFilename()));
		return Document.builder().qualityControl(qualityControl)
				.originalFilename(f.getOriginalFilename()).size(f.getSize())
				.contentType(f.getContentType()).extension(ext).storagePath(path)
				.uploadDate(LocalDateTime.now()).build();

	}

	/**
	 * Extrait l'extension d'un nom de fichier.
	 *
	 * @param name
	 *            le nom complet du fichier (doit contenir ou non un '.')
	 * @return l'extension du fichier (sans le point) en minuscules, ou chaîne vide si aucun point
	 *         n'est trouvé
	 */
	public static String getExt(String name) {
		int i = name.lastIndexOf('.');
		return i > 0 ? name.substring(i + 1).toLowerCase() : "";
	}
}
