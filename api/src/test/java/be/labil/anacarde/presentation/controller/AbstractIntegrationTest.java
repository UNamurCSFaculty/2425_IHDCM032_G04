package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.domain.model.*;
import be.labil.anacarde.infrastructure.persistence.*;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Classe de base pour les tests d'intégration qui nécessitent des utilisateurs et des rôles de test en base de données.
 */
public abstract class AbstractIntegrationTest {

	@Autowired
	protected JwtUtil jwtUtil;
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected RoleRepository roleRepository;
	@Autowired
	protected LanguageRepository languageRepository;
	@Autowired
	protected StoreRepository storeRepository;
	@Autowired
	protected ProductRepository productRepository;
	@Autowired
	protected AuctionRepository auctionRepository;
	@Autowired
	protected AuctionStrategyRepository auctionStrategyRepository;
	@Autowired
	protected UserDetailsService userDetailsService;

	private Language mainLanguage;
	private User mainTestUser;
	private User secondTestUser;
	private User producerTestUser;
	private Role userTestRole;
	private Role adminTestRole;
	private Store mainTestStore;
	private Product mainTestProduct;
	private Auction testAuction;
	private AuctionStrategy testAuctionStrategy;

	@Getter
	private Cookie jwtCookie;

	@BeforeEach
	public void setUp() {
		initUserDatabase();
		initJwtCookie();
	}

	@AfterEach
	public void tearDown() {
		auctionRepository.deleteAll();
		productRepository.deleteAll();
		storeRepository.deleteAll();
		userRepository.deleteAll();
		roleRepository.deleteAll();
	}

	/**
	 * Renvoie l'utilisateur de test principal, utilisé pour les requêtes (cookie JWT).
	 */
	public User getMainTestUser() {
		if (mainTestUser == null) {
			throw new IllegalStateException("Utilisateur de test non initialisé");
		}
		return mainTestUser;
	}

	/**
	 * Renvoie le second utilisateur de test.
	 */
	public User getSecondTestUser() {
		if (secondTestUser == null) {
			throw new IllegalStateException("Second utilisateur de test non initialisé");
		}
		return secondTestUser;
	}

	/**
	 * Renvoie le second utilisateur de test.
	 */
	public User getProducerTestUser() {
		if (producerTestUser == null) {
			throw new IllegalStateException("Second utilisateur de test non initialisé");
		}
		return producerTestUser;
	}

	/**
	 * Renvoie le rôle d'utilisateur de test.
	 */
	public Role getUserTestRole() {
		if (userTestRole == null) {
			throw new IllegalStateException("Rôle d'utilisateur non initialisé");
		}
		return userTestRole;
	}

	/**
	 * Renvoie le rôle d'administrateur de test.
	 */
	public Role getAdminTestRole() {
		if (adminTestRole == null) {
			throw new IllegalStateException("Rôle d'administrateur non initialisé");
		}
		return adminTestRole;
	}

	/**
	 * Renvoie la langue principale de test.
	 */
	public Language getMainLanguage() {
		if (mainLanguage == null) {
			throw new IllegalStateException("Langue principale non initialisée");
		}
		return mainLanguage;
	}

	/**
	 * Renvoie le magasin de test principal.
	 */
	public Store getMainTestStore() {
		if (mainTestStore == null) {
			throw new IllegalStateException("Magasin de test non initialisé");
		}
		return mainTestStore;
	}

	/**
	 * Renvoie le produit de test principal.
	 */
	public Product getMainTestProduct() {
		if (mainTestProduct == null) {
			throw new IllegalStateException("Produit de test non initialisé");
		}
		return mainTestProduct;
	}

	public Auction getTestAuction() {
		if (testAuction == null) {
			throw new IllegalStateException("Enchère de test non initialisée");
		}
		return testAuction;
	}

	public AuctionStrategy getTestAuctionStrategy() {
		if (testAuctionStrategy == null) {
			throw new IllegalStateException("Stratégie de test non initialisée");
		}
		return testAuctionStrategy;
	}

	/**
	 * Initialise la base de données des utilisateurs avec deux utilisateurs de test et les rôles associés.
	 */
	public void initUserDatabase() {
		userRepository.deleteAll();
		languageRepository.deleteAll();

		Language language = Language.builder().name("fr").build();
		mainLanguage = languageRepository.save(language);

		User user1 = Admin.builder().firstName("John").lastName("Doe").email("user@example.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.language(mainLanguage).enabled(true).build();

		User user2 = Admin.builder().firstName("Foo").lastName("Bar").email("foo@bar.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.language(mainLanguage).enabled(true).build();

		User producer = Producer.builder().firstName("Bruce").lastName("Banner").email("bruce@hulk.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.language(mainLanguage).enabled(true).agriculturalIdentifier("111-222-333").build();

		Role userRole = Role.builder().name("ROLE_USER").build();
		Role adminRole = Role.builder().name("ROLE_ADMIN").build();
		userTestRole = roleRepository.save(userRole);
		adminTestRole = roleRepository.save(adminRole);

		// Assurer la mise à jour bidirectionnelle TODO zak: quoi ??
		user1.addRole(userTestRole);
		user2.addRole(adminTestRole);
		user2.addRole(userTestRole);
		producer.addRole(userTestRole);

		mainTestUser = userRepository.save(user1);
		secondTestUser = userRepository.save(user2);
		producerTestUser = userRepository.save(producer);

		Point storeLocation = new GeometryFactory().createPoint(new Coordinate(2.3522, 48.8566));
		Store store = Store.builder().location(storeLocation).user(user1).build();
		mainTestStore = storeRepository.save(store);

		Product product = HarvestProduct.builder().producer((Producer) producerTestUser).store(mainTestStore)
				.deliveryDate(LocalDateTime.now()).weightKg(2000.0).build();
		mainTestProduct = productRepository.save(product);

		AuctionStrategy strategy = AuctionStrategy.builder().name("Meilleure offre").build();
		testAuctionStrategy = auctionStrategyRepository.save(strategy);

		Auction auction = Auction.builder().price(new BigDecimal("500.0")).productQuantity(10).active(true)
				.creationDate(LocalDateTime.now()).expirationDate(LocalDateTime.now()).product(product)
				.strategy(testAuctionStrategy).build();
		testAuction = auctionRepository.save(auction);
	}

	/**
	 * Génère un cookie JWT HTTP-only en utilisant les détails de l'utilisateur de test principal.
	 */
	protected void initJwtCookie() {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getMainTestUser().getEmail());
		String token = jwtUtil.generateToken(userDetails);
		jwtCookie = new Cookie("jwt", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
	}
}
