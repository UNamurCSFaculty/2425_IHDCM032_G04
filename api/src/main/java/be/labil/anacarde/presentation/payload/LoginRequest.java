package be.labil.anacarde.presentation.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Cette classe encapsule le nom d'utilisateur et le mot de passe requis pour l'authentification.
 */
public class LoginRequest {
    /** Nom d'utilisateur de l'utilisateur. */
    private String username;

    /** Mot de passe de l'utilisateur. */
    private String password;
}
