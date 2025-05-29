package be.labil.anacarde.application.service.storage;

import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.QualityControl;
import be.labil.anacarde.domain.model.User;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/** Service chargé d’enregistrer des documents et de renvoyer les entités persistables. */
public interface StorageService {

	/**
	 * Enregistre les fichiers puis renvoie la liste d’entités {@link Document} à persister.
	 *
	 * @param user
	 *            identifiant de l’utilisateur propriétaire
	 * @param files
	 *            fichiers uploadés (peut être vide)
	 */
	List<Document> storeAll(User user, List<MultipartFile> files);

	/**
	 * Enregistre les fichiers puis renvoie la liste d’entités {@link Document} à persister.
	 *
	 * @param qualityControl
	 *            identifiant du contrôle qualité
	 * @param files
	 *            fichiers uploadés (peut être vide)
	 */
	List<Document> storeAll(QualityControl qualityControl, List<MultipartFile> files);

	/** Retourne le flux du document (contrôle d’accès déjà fait). */
	InputStream get(Document doc);

	/** Supprime physiquement le document. */
	void delete(Document doc);
}
