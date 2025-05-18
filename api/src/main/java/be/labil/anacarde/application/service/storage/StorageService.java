package be.labil.anacarde.application.service.storage;

import be.labil.anacarde.domain.model.Document;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/** Service chargé d’enregistrer des documents et de renvoyer les entités persistables. */
public interface StorageService {

	/**
	 * Enregistre les fichiers puis renvoie la liste d’entités {@link Document} à persister.
	 *
	 * @param userId
	 *            identifiant de l’utilisateur propriétaire
	 * @param files
	 *            fichiers uploadés (peut être vide)
	 */
	List<Document> storeAll(Integer userId, List<MultipartFile> files) throws IOException;
}
