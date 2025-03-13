package be.labil.anacarde.application.service.impl;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.UserService;
import be.labil.anacarde.domain.dto.UserDto;
import be.labil.anacarde.domain.mapper.UserMapper;
import be.labil.anacarde.domain.model.Role;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.RoleRepository;
import be.labil.anacarde.infrastructure.persistence.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
/**
 * Ce service fournit des méthodes pour l'authentification et la gestion des utilisateurs, incluant
 * la création, la récupération, la mise à jour et la suppression des utilisateurs. Il utilise un
 * UserRepository pour la persistance, un UserMapper pour la conversion entre entités et DTO, et un
 * PasswordEncoder pour sécuriser les mots de passe.
 */
public class UserDetailServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder bCryptPasswordEncoder;

    /**
     * Recherche un utilisateur par son email. Si l'utilisateur n'est pas trouvé, une
     * UsernameNotFoundException est levée.
     *
     * @param email L'adresse email de l'utilisateur.
     * @return Un objet UserDetails contenant les informations d'authentification de l'utilisateur.
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec l'email fourni.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () ->
                                new UsernameNotFoundException(
                                        "Utilisateur non trouvé avec l'email : " + email));
    }

    /**
     * %* Crée un nouvel utilisateur à partir des informations fournies dans le DTO.
     *
     * @param userDto Le DTO contenant les informations de l'utilisateur.
     * @return Un UserDto représentant l'utilisateur créé.
     */
    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        }
        User user = userMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    /**
     * Recherche un utilisateur par son identifiant. Si aucun utilisateur n'est trouvé, une
     * ResourceNotFoundException est levée.
     *
     * @param id L'identifiant unique de l'utilisateur.
     * @return Un UserDto représentant l'utilisateur avec l'ID spécifié.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Integer id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return userMapper.toDto(user);
    }

    /**
     * Récupère tous les utilisateurs du repository et les convertit en une List de UserDto.
     *
     * @return Une List de UserDto représentant tous les utilisateurs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour un utilisateur avec les informations fournies dans le DTO.
     *
     * @param id L'identifiant unique de l'utilisateur à mettre à jour.
     * @param userDto Le DTO contenant les informations mises à jour de l'utilisateur.
     * @return Un UserDto représentant l'utilisateur mis à jour.
     */
    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        User existingUser =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        // Mets uniquement à jour les champs non nuls du DTO
        User updatedUser = userMapper.partialUpdate(userDto, existingUser);

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            updatedUser.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        }

        User saved = userRepository.save(updatedUser);
        return userMapper.toDto(saved);
    }

    /**
     * Supprime un utilisateur du repository.
     *
     * @param id L'identifiant unique de l'utilisateur à supprimer.
     */
    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé");
        }
        userRepository.deleteById(id);
    }

    /**
     * Ajoute un rôle à un utilisateur.
     *
     * @param userId L'identifiant unique de l'utilisateur.
     * @param roleName Le nom du rôle à ajouter.
     * @return Un UserDto représentant l'utilisateur mis à jour avec le nouveau rôle.
     * @throws ResourceNotFoundException si l'utilisateur ou le rôle n'est pas trouvé.
     */
    @Override
    public UserDto addRoleToUser(Integer userId, String roleName) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        Role role =
                roleRepository
                        .findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé"));
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Met à jour l'ensemble des rôles d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur.
     * @param roleNames La List des noms de rôle à attribuer à l'utilisateur.
     * @return Un UserDto représentant l'utilisateur mis à jour.
     * @throws ResourceNotFoundException si l'utilisateur ou l'un des rôles n'est pas trouvé.
     */
    @Override
    public UserDto updateUserRoles(Integer userId, List<String> roleNames) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Set<Role> newRoles =
                roleNames.stream()
                        .map(
                                roleName ->
                                        roleRepository
                                                .findByName(roleName)
                                                .orElseThrow(
                                                        () ->
                                                                new ResourceNotFoundException(
                                                                        "Rôle non trouvé: "
                                                                                + roleName)))
                        .collect(Collectors.toSet());

        user.setRoles(newRoles);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
