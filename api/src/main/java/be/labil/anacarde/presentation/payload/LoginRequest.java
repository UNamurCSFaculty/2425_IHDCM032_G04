package be.labil.anacarde.presentation.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Payload pour la requête d’authentification.
 * <p>
 * Contient le nom d’utilisateur et le mot de passe soumis lors de la tentative de connexion.
 */
@Getter
@Setter
public class LoginRequest {

	@NotNull(message = "Le nom d'utilisateur est obligatoire")
	private String username;

	@NotNull(message = "Le mot de passe est obligatoire")
	private String password;
}
