package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.BadRequestException;
import be.labil.anacarde.application.service.storage.StorageService;
import be.labil.anacarde.domain.dto.db.user.GoogleRegistrationDto;
import be.labil.anacarde.domain.mapper.UserDetailMapper;
import be.labil.anacarde.domain.model.AuthProvider;
import be.labil.anacarde.domain.model.User;
import be.labil.anacarde.infrastructure.persistence.DocumentRepository;
import be.labil.anacarde.infrastructure.persistence.LanguageRepository;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class GoogleAuthService {

	@Value("${google.client.id:dummy-client-id}")
	private String googleClientId;

	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final LanguageRepository languageRepo;
	private final UserDetailMapper userDetailMapper;
	private final StorageService storageService;
	private final DocumentRepository documentRepo;

	private GoogleIdTokenVerifier verifier;

	@PostConstruct
	private void initVerifier() {
		try {
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			var transport = GoogleNetHttpTransport.newTrustedTransport();
			this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
					.setAudience(List.of(googleClientId)).build();
		} catch (GeneralSecurityException | IOException e) {
			throw new IllegalStateException("Impossible d'initialiser GoogleIdTokenVerifier", e);
		}
	}

	/**
	 * Vérifie l'ID-token Google, crée ou met à jour l'utilisateur, stocke éventuels fichiers, et
	 * retourne un JWT.
	 */
	public String processGoogleRegistration(GoogleRegistrationDto dto, List<MultipartFile> files)
			throws GeneralSecurityException, IOException {

		// 1) Vérification du token OIDC
		GoogleIdToken idToken = verifier.verify(dto.getIdToken());
		if (idToken == null) {
			throw new BadCredentialsException("Token Google invalide");
		}

		var payload = idToken.getPayload();
		String email = payload.getEmail().trim().toLowerCase();
		String sub = payload.getSubject();
		if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
			throw new BadCredentialsException("Email non vérifié par Google");
		}

		// 2) Recherche d’un compte Google existant
		Optional<User> existingGoogle = userRepo.findByProviderAndProviderId(AuthProvider.GOOGLE,
				sub);
		User user;
		if (existingGoogle.isPresent()) {
			user = existingGoogle.get();
			updateExistingUser(user, dto, files);

		} else {
			// 3) Refuse si un compte LOCAL existe déjà avec ce même email
			if (userRepo.findByEmailAndProvider(email, AuthProvider.LOCAL).isPresent()) {
				throw new BadRequestException("Cet email est déjà utilisé par un compte local");
			}
			// 4) Création d’un nouvel utilisateur
			user = userDetailMapper.toEntity(dto);
			user.setEmail(email);
			user.setFirstName((String) payload.get("given_name"));
			user.setLastName((String) payload.get("family_name"));
			user.setProvider(AuthProvider.GOOGLE);
			user.setProviderId(sub);
			user.setEnabled(true);
			// mot de passe factice
			user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

			user = userRepo.save(user);
			saveDocuments(user, files);
		}

		// 5) Génération et retour du JWT
		return jwtUtil.generateToken(user);
	}

	private void updateExistingUser(User user, GoogleRegistrationDto dto,
			List<MultipartFile> files) {
		if (dto.getPhone() != null) {
			user.setPhone(dto.getPhone());
		}
		if (dto.getLanguageId() != null) {
			var lang = languageRepo.findById(dto.getLanguageId())
					.orElseThrow(() -> new BadRequestException("Langue introuvable"));
			user.setLanguage(lang);
		}
		if (dto.getAddress() != null) {
			user.setAddress(userDetailMapper.toEntity(dto).getAddress());
		}
		saveDocuments(user, files);
	}

	private void saveDocuments(User user, List<MultipartFile> files) {
		if (files != null && !files.isEmpty()) {
			var saved = storageService.storeAll(user, files);
			documentRepo.saveAll(saved);
			user.getDocuments().addAll(saved);
		}
	}
}
