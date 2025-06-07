package be.labil.anacarde.infrastructure.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring pour l’authentification via Google.
 * <p>
 * Définit et expose un bean {@link GoogleIdTokenVerifier} configuré pour valider les ID tokens émis
 * par Google, en utilisant l’audience spécifiée par l’identifiant client injecté.
 */
@Configuration
public class GoogleAuthConfig {

	@Value("${google.client-id}")
	private String googleClientId;

	/**
	 * Crée un bean {@link GoogleIdTokenVerifier} pour vérifier la validité des ID tokens Google.
	 * <p>
	 * Utilise un transport HTTP {@link NetHttpTransport} et la bibliothèque Jackson pour le parsing
	 * JSON, et restreint l’audience au client ID configuré.
	 *
	 * @return un {@code GoogleIdTokenVerifier} prêt à être injecté et utilisé pour
	 *         l’authentification des utilisateurs via leurs tokens Google
	 */
	@Bean
	public GoogleIdTokenVerifier googleIdTokenVerifier() {
		return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
				JacksonFactory.getDefaultInstance())
				.setAudience(Collections.singletonList(googleClientId)).build();
	}
}
