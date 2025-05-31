package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.model.User;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleAuthService {
	/**
	 * Vérifie l'ID-token Google, associe le compte Google à l'utilisateur existant (par email), et
	 * retourne un JWT pour la session.
	 *
	 * @param token
	 *            DTO contenant l'idToken issu du client Google.
	 * @return le token JWT de la session utilisateur.
	 * @throws GeneralSecurityException
	 *             si la vérification du token Google échoue.
	 * @throws IOException
	 *             si une erreur I/O survient pendant la vérification.
	 */
	User processGoogleRegistration(String token) throws GeneralSecurityException, IOException;
}
