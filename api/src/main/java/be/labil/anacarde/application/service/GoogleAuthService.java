package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.user.GoogleRegistrationDto;
import be.labil.anacarde.domain.model.AuthProvider;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GoogleAuthService {

	private final GoogleIdTokenVerifier tokenVerifier;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	/**
	 * Vérifie l'ID-token Google, associe le compte Google à l'utilisateur existant (par email), et
	 * retourne un JWT pour la session.
	 *
	 * @param dto
	 *            DTO contenant l'idToken issu du client Google.
	 * @return le token JWT de la session utilisateur.
	 * @throws GeneralSecurityException
	 *             si la vérification du token Google échoue.
	 * @throws IOException
	 *             si une erreur I/O survient pendant la vérification.
	 */
	public String processGoogleRegistration(GoogleRegistrationDto dto)
			throws GeneralSecurityException, IOException {

		// 1. Vérification de l'ID-token Google
		GoogleIdToken idToken = GoogleIdToken.parse(tokenVerifier.getJsonFactory(),
				dto.getIdToken());

		if (!tokenVerifier.verify(idToken)) {
			throw new GeneralSecurityException("Invalid Google ID token.");
		}

		Payload payload = idToken.getPayload();
		String email = payload.getEmail();
		String providerId = payload.getSubject(); // le "sub" de Google

		// 2. Recherche de l'utilisateur local existant
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Aucun utilisateur trouvé pour l'email: " + email));

		// 3. Mise à jour du provider si nécessaire
		if (user.getProvider() != AuthProvider.GOOGLE || !providerId.equals(user.getProviderId())) {
			user.setProvider(AuthProvider.GOOGLE);
			user.setProviderId(providerId);
			userRepository.save(user);
		}

		// 4. Génération du JWT à partir de votre JwtUtil
		// JwtUtil.generateToken attend un UserDetails, et User implémente UserDetails
		return jwtUtil.generateToken(user);
	}
}
