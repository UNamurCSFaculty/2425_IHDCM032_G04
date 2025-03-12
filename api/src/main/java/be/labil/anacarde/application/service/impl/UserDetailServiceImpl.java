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
 * @brief Implementation of UserDetailsService and UserService for managing user operations.
 *     <p>This service provides methods for user authentication and user management, including
 *     creating, retrieving, updating, and deleting users. It leverages a UserRepository for
 *     persistence, a UserMapper for converting between entities and DTOs, and a PasswordEncoder for
 *     securing passwords.
 */
public class UserDetailServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder bCryptPasswordEncoder;

    /**
     * @brief Loads the user details by email for authentication purposes.
     *     <p>This method searches for a user by their email. If the user is not found, a
     *     UsernameNotFoundException is thrown.
     * @param email The email address of the user.
     * @return A UserDetails object containing the user's authentication information.
     * @throws UsernameNotFoundException if no user is found with the provided email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User Not Found with email: " + email));
    }

    /**
     * @brief Creates a new user in the system.
     *     <p>This method encodes the user's password (if provided), converts the UserDto to a User
     *     entity, saves the entity in the repository, and returns the saved user as a UserDto.
     * @param userDto The data transfer object containing user information.
     * @return A UserDto representing the newly created user.
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
     * @brief Retrieves a user by their unique identifier.
     *     <p>This method finds a user by ID. If no user is found, a ResourceNotFoundException is
     *     thrown.
     * @param id The unique identifier of the user.
     * @return A UserDto representing the user with the specified ID.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Integer id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    /**
     * @brief Lists all users in the system.
     *     <p>This method retrieves all users from the repository and converts them into a list of
     *     UserDto objects.
     * @return A list of UserDto objects representing all users.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * @brief Updates an existing user.
     *     <p>This method finds the existing user by ID, applies partial updates from the provided
     *     UserDto, encodes the password if provided, saves the updated user, and returns the
     *     updated user as a UserDto.
     * @param id The unique identifier of the user to update.
     * @param userDto The data transfer object containing updated user information.
     * @return A UserDto representing the updated user.
     */
    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        User existingUser =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // Update only the fields that are not null in the DTO
        User updatedUser = userMapper.partialUpdate(userDto, existingUser);
        // If password is provided in the DTO, encode and update it
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            updatedUser.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        }

        User saved = userRepository.save(updatedUser);
        return userMapper.toDto(saved);
    }

    /**
     * @brief Deletes a user from the system.
     *     <p>This method checks if a user with the specified ID exists. If not, it throws a
     *     ResourceNotFoundException. Otherwise, it deletes the user from the repository.
     * @param id The unique identifier of the user to delete.
     */
    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * @brief Adds a role to an existing user.
     *     <p>This method retrieves the user by the given identifier and the role by its name. If
     *     both exist, it adds the role to the user's set of roles and saves the updated user.
     * @param userId The unique identifier of the user.
     * @param roleName The name of the role to add.
     * @return A UserDto representing the updated user with the new role.
     * @throws ResourceNotFoundException if either the user or the role is not found.
     */
    @Override
    public UserDto addRoleToUser(Integer userId, String roleName) {
        // Retrieve the user; throw an exception if not found
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // Retrieve the role by its name; throw an exception if not found
        Role role =
                roleRepository
                        .findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        // Add the role to the user's set of roles
        user.getRoles().add(role);
        // Save and return the updated user as a DTO
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
