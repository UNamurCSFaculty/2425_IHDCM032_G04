package be.labil.anacarde.domain.dto;

/**
 * @brief Interface defining validation groups for user operations.
 *     <p>This interface declares different validation groups used to differentiate validation rules
 *     for user operations. It includes the Create group for user creation and the Update group for
 *     user updates.
 */
public interface ValidationGroups {

    /**
     * @brief Validation group used for creating a new user.
     */
    interface Create {}

    /**
     * @brief Validation group used for updating an existing user.
     */
    interface Update {}
}
