
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserListDto;
import be.labil.anacarde.domain.dto.write.user.UserUpdateDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des
 * informations d'utilisateur.
 */
public interface UserService {

	/**
	 * Crée un nouvel utilisateur dans le système en utilisant le UserDto fourni.
	 *
	 * @param dto
	 *            Le UserDto contenant les informations du nouvel utilisateur.
	 * @return Un UserDto représentant l'utilisateur créé.
	 */
	UserDetailDto createUser(UserUpdateDto dto, List<MultipartFile> files);

	/**
	 * Retourne l'utilisateur correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique de l'utilisateur.
	 * @return Un UserDto représentant l'utilisateur avec l'ID spécifié.
	 */
	UserDetailDto getUserById(Integer id);

	/**
	 * Récupère tous les utilisateurs du système.
	 *
	 * @return Une List de UserDto représentant tous les utilisateurs.
	 */
	List<UserListDto> listUsers();

	/**
	 * Met à jour l'utilisateur identifié par l'ID donné avec les informations fournies dans le
	 * UserDto.
	 *
	 * @param id
	 *            L'identifiant unique de l'utilisateur à mettre à jour.
	 * @param userDetailDto
	 *            Le UserDto contenant les informations mises à jour.
	 * @return Un UserDto représentant l'utilisateur mis à jour.
	 */
	UserDetailDto updateUser(Integer id, UserUpdateDto userDetailDto);

	/**
	 * Supprime l'utilisateur identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique de l'utilisateur à supprimer.
	 */
	void deleteUser(Integer id);

	/**
	 * Ajoute un rôle à l'utilisateur identifié par l'ID fourni.
	 *
	 * @param userId
	 *            L'identifiant unique de l'utilisateur.
	 * @param roleName
	 *            Le nom du rôle à ajouter.
	 * @return Un UserDto représentant l'utilisateur mis à jour.
	 */
	UserDetailDto addRoleToUser(Integer userId, String roleName);

	/**
	 * Met à jour l'ensemble des rôles de l'utilisateur identifié par l'ID fourni.
	 *
	 * @param userId
	 *            L'identifiant unique de l'utilisateur.
	 * @param roleNames
	 *            La List des noms de rôle à attribuer à l'utilisateur.
	 * @return Un UserDto représentant l'utilisateur mis à jour.
	 */
	UserDetailDto updateUserRoles(Integer userId, List<String> roleNames);

	/**
	 * Vérifie si un utilisateur existe déjà dans le système en fonction de son adresse e-mail.
	 * 
	 * @param email
	 * @return true si l'adresse e-mail existe déjà, false sinon.
	 */
	boolean emailExists(String email);

	/**
	 * Vérifie si un numéro de téléphone existe déjà dans le système.
	 *
	 * @param phone
	 * @return true si le numéro de téléphone existe déjà, false sinon.
	 */
	boolean phoneExists(String phone);
}
