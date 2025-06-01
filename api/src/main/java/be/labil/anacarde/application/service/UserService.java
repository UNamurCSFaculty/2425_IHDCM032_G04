
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserListDto;
import be.labil.anacarde.domain.dto.write.user.create.UserCreateDto;
import be.labil.anacarde.domain.dto.write.user.update.UserUpdateDto;
import be.labil.anacarde.presentation.controller.enums.UserType;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
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
	UserDetailDto createUser(UserCreateDto dto, List<MultipartFile> files);

	/**
	 * Retourne l'utilisateur correspondant à l'ID fourni.
	 *
	 * @param userId
	 *            L'identifiant unique de l'utilisateur.
	 * @return Un UserDto représentant l'utilisateur avec l'ID spécifié.
	 */
	UserDetailDto getUserById(Integer userId);

	/**
	 * Récupère les utilisateurs du système.
	 *
	 * @param type
	 *            Le types d'utilisateur à filtrer.
	 * @return Une List de UserDto représentant les utilisateurs.
	 */
	List<UserListDto> listUsers(UserType type);

	/**
	 * Met à jour l'utilisateur identifié par l'ID donné avec les informations fournies dans le
	 * UserDto.
	 *
	 * @param userId
	 *            L'identifiant unique de l'utilisateur à mettre à jour.
	 * @param userUpdateDto
	 *            Le UserDto contenant les informations mises à jour.
	 * @return Un UserDto représentant l'utilisateur mis à jour.
	 */
	@PreAuthorize("@authz.isAdmin(principal) or (principal.id == #userId)")
	UserDetailDto updateUser(Integer userId, @Param("userUpdateDto") UserUpdateDto userUpdateDto);

	/**
	 * Supprime l'utilisateur identifié par l'ID donné du système.
	 *
	 * @param userId
	 *            L'identifiant unique de l'utilisateur à supprimer.
	 */
	@PreAuthorize("@authz.isAdmin(principal)")
	void deleteUser(Integer userId);

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
