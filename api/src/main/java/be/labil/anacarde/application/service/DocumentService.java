package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.DocumentStorageException;
import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.DocumentDto;
import java.io.InputStream;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service métier pour gérer les documents.
 *
 * - CRUD des métadonnées via {@link DocumentDto}. - Streaming du contenu brut.
 */
public interface DocumentService {

	/**
	 * Crée un nouveau document en même temps que son upload.
	 *
	 * @param userId
	 *            Identifiant de l'utilisateur propriétaire du document.
	 * @param file
	 *            Le fichier à téléverser.
	 * @return Le DocumentDto complet (id, uploadDate, storagePath, etc.).
	 */
	@PreAuthorize("@authz.isAdmin(principal) or #userId == principal.id")
	DocumentDto createDocumentUser(Integer userId, MultipartFile file);

	/**
	 * Crée un nouveau document en même temps que son upload.
	 *
	 * @param qualityControlId
	 *            Identifiant du contrôle qualité propriétaire du document.
	 * @param file
	 *            Le fichier à téléverser.
	 * @return Le DocumentDto complet (id, uploadDate, storagePath, etc.).
	 */
	DocumentDto createDocumentQualityControl(Integer qualityControlId, MultipartFile file);

	/**
	 * Recherche les méta-infos d’un document.
	 *
	 * @param id
	 *            Identifiant du document.
	 * @return Les données du document.
	 * @throws ResourceNotFoundException
	 *             si l’ID n’existe pas.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or @ownership.isDocumentOwner(#id, principal.id)")
	DocumentDto getDocumentById(Integer id);

	/**
	 * Supprime un document : - supprime le fichier via StorageService, - supprime la ligne en base.
	 *
	 * @param id
	 *            Identifiant du document.
	 * @throws ResourceNotFoundException
	 *             si l’ID n’existe pas.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or @ownership.isDocumentOwner(#id, principal.id)")
	void deleteDocument(Integer id);

	/**
	 * Liste tous les documents d’un utilisateur.
	 *
	 * @param userId
	 *            Identifiant du propriétaire.
	 * @return Liste de DocumentDto.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or #userId == principal.id")
	List<DocumentDto> listDocumentsByUser(Integer userId);

	/**
	 * Fournit un flux {@link InputStream} sur le contenu brut du document.
	 *
	 * @param id
	 *            Identifiant du document.
	 * @return Flux du fichier.
	 * @throws ResourceNotFoundException
	 *             si l’ID n’existe pas.
	 * @throws DocumentStorageException
	 *             si la lecture du fichier échoue.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or @ownership.isDocumentOwner(#id, principal.id)")
	InputStream streamDocumentContent(Integer id) throws DocumentStorageException;
}
