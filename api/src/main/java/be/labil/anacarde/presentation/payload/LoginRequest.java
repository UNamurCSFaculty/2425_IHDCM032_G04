package be.labil.anacarde.presentation.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Cette classe encapsule le nom d'utilisateur et le mot de passe requis pour l'authentification.
 */
public class LoginRequest {

	@NotNull(message = "Le nom d'utilisateur est obligatoire")
	private String username;

	@NotNull(message = "Le mot de passe est obligatoire")
	private String password;
}
