package be.labil.anacarde.presentation.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * @brief LoginRequest class used for login operations.
 *
 * This class encapsulates the username and password required for user authentication.
 */
public class LoginRequest {
    /**
     * @brief The username of the user.
     */
    private String username;
    /**
     * @brief The password of the user.
     */
    private String password;
}
