
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.UserDto;
import java.util.List;

/**
 * Cette interface définit des méthodes pour créer, récupérer, mettre à jour et supprimer les informations d'un
 * utilisateur. Les méthodes opèrent sur des objets UserDto, qui servent de data transfer objects pour les données
 * utilisateur.
 */
public interface UserService {

	/**
	 * Crée un nouvel utilisateur dans le système en utilisant le UserDto fourni.
	 *
	 * @param userDto
	 *            Le UserDto contenant les informations du nouvel utilisateur.
	 * @return Un UserDto représentant l'utilisateur créé.
	 */
	UserDto createUser(UserDto userDto);

	/**
	 * Retourne l'utilisateur correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique de l'utilisateur.
	 * @return Un UserDto représentant l'utilisateur avec l'ID spécifié.
	 */
	UserDto getUserById(Integer id);

	/**
	 * Récupère tous les utilisateurs du système.
	 *
	 * @return Une List de UserDto représentant tous les utilisateurs.
	 */
	List<UserDto> listUsers();

	/**
	 * Met à jour l'utilisateur identifié par l'ID donné avec les informations fournies dans le UserDto.
	 *
	 * @param id
	 *            L'identifiant unique de l'utilisateur à mettre à jour.
	 * @param userDto
	 *            Le UserDto contenant les informations mises à jour.
	 * @return Un UserDto représentant l'utilisateur mis à jour.
	 */
	UserDto updateUser(Integer id, UserDto userDto);

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
	UserDto addRoleToUser(Integer userId, String roleName);

	/**
	 * Met à jour l'ensemble des rôles de l'utilisateur identifié par l'ID fourni.
	 *
	 * @param userId
	 *            L'identifiant unique de l'utilisateur.
	 * @param roleNames
	 *            La List des noms de rôle à attribuer à l'utilisateur.
	 * @return Un UserDto représentant l'utilisateur mis à jour.
	 */
	UserDto updateUserRoles(Integer userId, List<String> roleNames);
}
