package be.labil.anacarde.presentation.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/* This class encapsulates the username and password required for user authentication. */
public class LoginRequest {
    /** The username of the user. */
    private String username;

    /** The password of the user. */
    private String password;
}
