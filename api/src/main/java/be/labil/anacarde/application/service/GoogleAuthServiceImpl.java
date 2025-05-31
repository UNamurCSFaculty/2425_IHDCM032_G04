package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ApiErrorCode;
import be.labil.anacarde.application.exception.ApiErrorException;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

	private final GoogleIdTokenVerifier tokenVerifier;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	@Override
	public User processGoogleRegistration(String googleToken)
			throws GeneralSecurityException, IOException {

		// 1. Vérification de l'ID-token Google
		try {
			GoogleIdToken idToken = GoogleIdToken.parse(tokenVerifier.getJsonFactory(),
					googleToken);

			if (!tokenVerifier.verify(idToken)) {
				throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
						"invalid_google_token", "L'ID-token Google fourni est invalide ou expiré.");
			}

			Payload payload = idToken.getPayload();
			String email = payload.getEmail();

			// 2. Recherche de l'utilisateur local existant
			User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiErrorException(
					HttpStatus.UNAUTHORIZED, ApiErrorCode.ACCESS_UNAUTHORIZED.code(),
					"gmail_account_not_found",
					"Aucun compte utilisateur associé à cette adresse email. Veuillez vous enregistrer d'abord."));

			return user;
		} catch (Exception e) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST.code(),
					"invalid_google_token", "L'ID-token Google fourni est invalide ou expiré.");
		}

	}
}
