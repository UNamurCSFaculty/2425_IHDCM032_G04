package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.UserDto;

import java.util.List;

/**
 * @brief Interface for managing user operations.
 *
 * This interface defines methods for creating, retrieving, updating, and deleting user information.
 * The methods operate on UserDto objects, which serve as data transfer objects for user data.
 */
public interface UserService {

    /**
     * @brief Creates a new user.
     *
     * This method creates a new user in the system using the provided UserDto.
     *
     * @param userDto The UserDto containing information for the new user.
     * @return A UserDto representing the newly created user.
     */
    UserDto createUser(UserDto userDto);

    /**
     * @brief Retrieves a user by their ID.
     *
     * This method returns a user matching the given ID.
     *
     * @param id The unique identifier of the user.
     * @return A UserDto representing the user with the specified ID.
     */
    UserDto getUserById(Integer id);

    /**
     * @brief Lists all users.
     *
     * This method returns a list of all users in the system.
     *
     * @return A List of UserDto objects representing all users.
     */
    List<UserDto> listUsers();

    /**
     * @brief Updates an existing user.
     *
     * This method updates the user identified by the given ID with the provided information in UserDto.
     *
     * @param id The unique identifier of the user to update.
     * @param userDto The UserDto containing updated information.
     * @return A UserDto representing the updated user.
     */
    UserDto updateUser(Integer id, UserDto userDto);

    /**
     * @brief Deletes a user.
     *
     * This method deletes the user identified by the given ID from the system.
     *
     * @param id The unique identifier of the user to delete.
     */
    void deleteUser(Integer id);

    /**
     * @brief Adds a role to a user.
     *
     * This method adds a role to the user identified by the given ID.
     *
     * @param userId The unique identifier of the user.
     * @param roleName The name of the role to add.
     * @return A UserDto representing the updated user.
     */
    UserDto addRoleToUser(Integer userId, String roleName);
}