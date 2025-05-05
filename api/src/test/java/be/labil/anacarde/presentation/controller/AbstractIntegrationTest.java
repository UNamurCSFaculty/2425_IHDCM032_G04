package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.DatabaseService;
import be.labil.anacarde.domain.model.*;
import be.labil.anacarde.infrastructure.persistence.*;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
	protected BidRepository bidRepository;
	@Autowired
	protected TradeStatusRepository TradeStatusRepository;
	@Autowired
	protected UserDetailsService userDetailsService;
	@Autowired
	protected FieldRepository fieldRepository;
	@Autowired
	protected CooperativeRepository cooperativeRepository;
	@Autowired
	protected RegionRepository regionRepository;
	@Autowired
	protected DocumentRepository documentRepository;
	@Autowired
	protected QualityRepository qualityRepository;
	@Autowired
	protected ContractOfferRepository contractOfferRepository;
	@Autowired
	protected QualityControlRepository qualityControlRepository;
	@Autowired
	protected DatabaseService databaseService;

	private Language mainLanguage;
	private User mainTestUser;
	private User secondTestUser;
	private User producerTestUser;
	private User secondTestProducer;
	private User transformerTestUser;
	private User mainTestCarrier;
	private Role userTestRole;
	private Role adminTestRole;
	private Store mainTestStore;
	private Product testHarvestProduct;
	private Product testTransformedProduct;
	private Auction testAuction;
	private AuctionStrategy testAuctionStrategy;
	private Bid testBid;
	private TradeStatus testBidStatus;
	private TradeStatus testAuctionStatus;
	private Field mainTestField;
	private Cooperative mainTestCooperative;
	private Region mainTestRegion;
	private Document mainTestDocument;
	private Quality mainTestQuality;
	private ContractOffer mainTestContractOffer;
	private QualityControl mainTestQualityControl;

	@Getter
	private Cookie jwtCookie;

	@BeforeEach
	public void setUp() {
		initUserDatabase();
		initJwtCookie();
	}

	@AfterEach
	public void tearDown() {
		databaseService.dropDatabase();
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
	 * Renvoie un utilisateur producteur de test.
	 */
	public User getProducerTestUser() {
		if (producerTestUser == null) {
			throw new IllegalStateException("Second utilisateur de test non initialisé");
		}
		return producerTestUser;
	}

	/**
	 * Renvoie un second utilisateur producteur de test.
	 */
	public User getSecondTestProducer() {
		if (secondTestProducer == null) {
			throw new IllegalStateException("Second utilisateur de test non initialisé");
		}
		return secondTestProducer;
	}

	/**
	 * Renvoie un utilisateur transformateur de test.
	 */
	public User getTransformerTestUser() {
		if (transformerTestUser == null) {
			throw new IllegalStateException("Second utilisateur de test non initialisé");
		}
		return transformerTestUser;
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
	 * Renvoie le produit de récolte de test.
	 */
	public Product getTestHarvestProduct() {
		if (testHarvestProduct == null) {
			throw new IllegalStateException("Produit de test non initialisé");
		}
		return testHarvestProduct;
	}

	/**
	 * Renvoie le produit transformé de test.
	 */
	public Product getTestTransformedProduct() {
		if (testTransformedProduct == null) {
			throw new IllegalStateException("Produit de test non initialisé");
		}
		return testTransformedProduct;
	}

	public Auction getTestAuction() {
		if (testAuction == null) {
			throw new IllegalStateException("Enchère de test non initialisée");
		}
		return testAuction;
	}

	public Bid getTestBid() {
		if (testBid == null) {
			throw new IllegalStateException("Enchère de test non initialisée");
		}
		return testBid;
	}

	public TradeStatus getTestBidStatus() {
		if (testBidStatus == null) {
			throw new IllegalStateException("Statut d'offre non initialisé");
		}
		return testBidStatus;
	}

	public TradeStatus getTestAuctionStatus() {
		if (testAuctionStatus == null) {
			throw new IllegalStateException("Statut d'enchère non initialisé");
		}
		return testAuctionStatus;
	}

	public AuctionStrategy getTestAuctionStrategy() {
		if (testAuctionStrategy == null) {
			throw new IllegalStateException("Stratégie de test non initialisée");
		}
		return testAuctionStrategy;
	}

	public Field getMainTestField() {
		if (mainTestField == null) {
			throw new IllegalStateException("Champs de test non initialisé");
		}
		return mainTestField;
	}

	/**
	 * Renvoie une coopérative de test.
	 */
	public Cooperative getMainTestCooperative() {
		if (mainTestCooperative == null) {
			throw new IllegalStateException("Cooperative de test non initialisée");
		}
		return mainTestCooperative;
	}

	/**
	 * Renvoie une région de test.
	 */
	public Region getMainTestRegion() {
		if (mainTestRegion == null) {
			throw new IllegalStateException("Région de test non initialisée");
		}
		return mainTestRegion;
	}

	/**
	 * Renvoie un transporter de test.
	 */
	public User getMainTestCarrier() {
		if (mainTestCarrier == null) {
			throw new IllegalStateException("Transporteur de test non initialisée");
		}
		return mainTestCarrier;
	}

	/**
	 * Renvoie un Document de test.
	 */
	public Document getMainTestDocument() {
		if (mainTestDocument == null) {
			throw new IllegalStateException("Document de test non initialisé");
		}
		return mainTestDocument;
	}

	/**
	 * Renvoie une Qualité de test.
	 */
	public Quality getMainTestQuality() {
		if (mainTestQuality == null) {
			throw new IllegalStateException("Qualité de test non initialisée");
		}
		return mainTestQuality;
	}

	public ContractOffer getMainTestContractOffer() {
		if (mainTestQuality == null) {
			throw new IllegalStateException("Contrat de test non initialisé");
		}
		return mainTestContractOffer;
	}

	/**
	 * Renvoie un control de qualité de test.
	 */
	public QualityControl getMainTestQualityControl() {
		if (mainTestQualityControl == null) {
			throw new IllegalStateException("Transporteur de test non initialisée");
		}
		return mainTestQualityControl;
	}

	/**
	 * Initialise la base de données des utilisateurs avec deux utilisateurs de test et les rôles associés.
	 */
	public void initUserDatabase() {
		// userRepository.deleteAll();
		// languageRepository.deleteAll();

		Language language = Language.builder().name("fr").build();
		mainLanguage = languageRepository.save(language);

		// A simple region
		Region region = Region.builder().name("sud").build();
		mainTestRegion = regionRepository.save(region);

		User user1 = Admin.builder().firstName("John").lastName("Doe").email("user@example.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.phone("+2290197000000").language(mainLanguage).enabled(true).build();

		User user2 = Admin.builder().firstName("Foo").lastName("Bar").email("foo@bar.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.phone("+2290197000001").language(mainLanguage).enabled(true).build();

		User producer = Producer.builder().firstName("Bruce").lastName("Banner").email("bruce@hulk.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.phone("+2290197000002").language(mainLanguage).enabled(true).agriculturalIdentifier("111-222-333")
				.build();

		User producer2 = Producer.builder().firstName("Steve").lastName("Rogers").email("steve@captain.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.phone("+2290197000003").language(mainLanguage).enabled(true).agriculturalIdentifier("000-111-222")
				.build();

		User transformer = Transformer.builder().firstName("Scott").lastName("Summers").email("scott@xmen.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.phone("+2290197000004").language(mainLanguage).enabled(true).build();

		User qualityInspector = QualityInspector.builder().firstName("Inspector").lastName("Best")
				.email("inspector@best.com").password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
				.phone("+2290197000005").registrationDate(LocalDateTime.now()).language(mainLanguage).enabled(true)
				.build();

		Set regions = new HashSet<>();
		regions.add(mainTestRegion);
		User carrier = Carrier.builder().firstName("Pierre").lastName("Verse").email("pierre@verse.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").registrationDate(LocalDateTime.now())
				.phone("+2290197000006").language(mainLanguage).enabled(true).regions(regions)
				.pricePerKm(new BigDecimal(100)).build();

		Role userRole = Role.builder().name("ROLE_USER").build();
		Role adminRole = Role.builder().name("ROLE_ADMIN").build();
		userTestRole = roleRepository.save(userRole);
		adminTestRole = roleRepository.save(adminRole);

		// Assurer la mise à jour bidirectionnelle TODO zak: quoi ??
		user1.addRole(userTestRole);
		user2.addRole(adminTestRole);
		user2.addRole(userTestRole);
		producer.addRole(userTestRole);
		transformer.addRole(userTestRole);

		mainTestUser = userRepository.save(user1);
		secondTestUser = userRepository.save(user2);
		producerTestUser = userRepository.save(producer);
		secondTestProducer = userRepository.save(producer2);
		transformerTestUser = userRepository.save(transformer);
		mainTestCarrier = userRepository.save(carrier);
		qualityInspector = userRepository.save(qualityInspector);

		Point storeLocation = new GeometryFactory().createPoint(new Coordinate(2.3522, 48.8566));
		Store store = Store.builder().name("Nassara").location(storeLocation).user(mainTestUser).build();
		mainTestStore = storeRepository.save(store);

		// Fields
		// A field binded to main producer
		Point pointField = new GeometryFactory().createPoint(new Coordinate(2.3522, 48.8566));
		Field field = Field.builder().producer((Producer) producerTestUser).identifier("FIELD-001").location(pointField)
				.build();

		mainTestField = fieldRepository.save(field);

		// A field to another producer
		Point pointField2 = new GeometryFactory().createPoint(new Coordinate(1.198, 10.300));
		Field field2 = Field.builder().producer((Producer) producerTestUser).identifier("FIELD-002")
				.location(pointField2).build();
		fieldRepository.save(field2);

		// A harvest product
		Product productHarvest = HarvestProduct.builder().producer((Producer) producerTestUser).store(mainTestStore)
				.deliveryDate(LocalDateTime.now()).weightKg(2000.0).field(mainTestField).build();
		testHarvestProduct = productRepository.save(productHarvest);

		// A transformed product
		Product productTransform = TransformedProduct.builder().transformer((Transformer) transformerTestUser)
				.deliveryDate(LocalDateTime.now()).identifier("XYZ").location("Zone B").weightKg(2000.0).build();
		testTransformedProduct = productRepository.save(productTransform);

		AuctionStrategy strategy = AuctionStrategy.builder().name("Meilleure offre").build();
		testAuctionStrategy = auctionStrategyRepository.save(strategy);

		TradeStatus auctionStatusOpen = TradeStatus.builder().name("Ouvert").build();
		testAuctionStatus = TradeStatusRepository.save(auctionStatusOpen);

		TradeStatus auctionStatusConcluded = TradeStatus.builder().name("Conclu").build();
		TradeStatusRepository.save(auctionStatusConcluded);

		TradeStatus auctionStatusExpired = TradeStatus.builder().name("Expiré").build();
		TradeStatusRepository.save(auctionStatusExpired);

		// An auction with a harvest product
		Auction auction = Auction.builder().price(new BigDecimal("500.0")).productQuantity(10).active(true)
				.creationDate(LocalDateTime.now()).expirationDate(LocalDateTime.now()).product(productHarvest)
				.strategy(testAuctionStrategy).trader((Trader) producer).status(auctionStatusOpen).build();
		testAuction = auctionRepository.save(auction);

		// An auction with a transformed product
		Auction auction2 = Auction.builder().price(new BigDecimal("10000.0")).productQuantity(1000).active(true)
				.creationDate(LocalDateTime.now()).expirationDate(LocalDateTime.now()).product(productTransform)
				.strategy(testAuctionStrategy).trader((Trader) producer).status(auctionStatusOpen).build();
		auctionRepository.save(auction2);

		// An auction from another user
		Auction auction3 = Auction.builder().price(new BigDecimal("777.0")).productQuantity(777).active(true)
				.creationDate(LocalDateTime.now()).expirationDate(LocalDateTime.now()).product(productTransform)
				.strategy(testAuctionStrategy).trader((Trader) transformer).status(auctionStatusOpen).build();
		auctionRepository.save(auction3);

		// A "deleted" auction (= inactive)
		Auction auction4 = Auction.builder().price(new BigDecimal("999.0")).productQuantity(999).active(false)
				.creationDate(LocalDateTime.now()).expirationDate(LocalDateTime.now()).product(productTransform)
				.strategy(testAuctionStrategy).trader((Trader) producer).status(auctionStatusOpen).build();
		auctionRepository.save(auction4);

		TradeStatus bidStatusPending = TradeStatus.builder().name("En cours").build();
		testBidStatus = TradeStatusRepository.save(bidStatusPending);

		TradeStatus bidStatusAccepted = TradeStatus.builder().name("Accepté").build();
		TradeStatusRepository.save(bidStatusAccepted);

		// A bid on an auction
		Bid bid = Bid.builder().amount(new BigDecimal("10.0")).creationDate(LocalDateTime.now()).auction(auction)
				.trader((Trader) producer).status(testBidStatus).build();
		testBid = bidRepository.save(bid);

		// A bid on a different auction
		Bid bid2 = Bid.builder().amount(new BigDecimal("500.0")).creationDate(LocalDateTime.now()).auction(auction2)
				.trader((Trader) producer).status(testBidStatus).build();
		bidRepository.save(bid2);

		// A cooperative who has for president 'producer'
		Cooperative cooperative = Cooperative.builder().name("Coopérative Agricole de Parakou")
				.address("Quartier Albarika, Rue 12, Parakou, Bénin").president((Producer) producer)
				.creationDate(LocalDateTime.of(2025, 4, 7, 12, 0)).build();
		mainTestCooperative = cooperativeRepository.save(cooperative);

		Producer p = (Producer) producer;
		p.setCooperative(mainTestCooperative);
		producerTestUser = userRepository.save(p);

		// A document with a qualityInspector
		Document document = Document.builder().format("text").type("TEXT").storagePath("/storage")
				.user(qualityInspector).uploadDate(LocalDateTime.now()).build();
		mainTestDocument = documentRepository.save(document);

		// A quality
		Quality quality = Quality.builder().name("WW160").build();
		mainTestQuality = qualityRepository.save(quality);

		// A contract
		ContractOffer contractOffer = ContractOffer.builder().status("Accepted").pricePerKg(new BigDecimal("20.0"))
				.creationDate(LocalDateTime.now()).endDate(LocalDateTime.now()).seller((Trader) producer)
				.buyer((Trader) transformer).quality(quality).build();
		mainTestContractOffer = contractOfferRepository.save(contractOffer);

		QualityControl qualityControl = QualityControl.builder().identifier("QC-001")
				.controlDate(LocalDateTime.of(2025, 4, 7, 10, 0)).granularity(0.5f).korTest(0.8f).humidity(12.5f)
				.qualityInspector((QualityInspector) qualityInspector).product(productTransform).quality(quality)
				.document(document).build();

		mainTestQualityControl = qualityControlRepository.save(qualityControl);

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
