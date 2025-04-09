
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.user.UserDetailDto;
import be.labil.anacarde.domain.dto.user.UserListDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des informations
 * d'utilisateur.
 */
public interface UserService {

	/**
	 * Crée un nouvel utilisateur dans le système en utilisant le UserDto fourni.
	 *
	 * @param userDetailDto
	 *            Le UserDto contenant les informations du nouvel utilisateur.
	 * @return Un UserDto représentant l'utilisateur créé.
	 */
	UserDetailDto createUser(UserDetailDto userDetailDto);

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
	 * Met à jour l'utilisateur identifié par l'ID donné avec les informations fournies dans le UserDto.
	 *
	 * @param id
	 *            L'identifiant unique de l'utilisateur à mettre à jour.
	 * @param userDetailDto
	 *            Le UserDto contenant les informations mises à jour.
	 * @return Un UserDto représentant l'utilisateur mis à jour.
	 */
	UserDetailDto updateUser(Integer id, UserDetailDto userDetailDto);

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
}
