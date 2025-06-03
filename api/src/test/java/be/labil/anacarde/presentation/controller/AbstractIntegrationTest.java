package be.labil.anacarde.presentation.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import be.labil.anacarde.TestRedisMocks;
import be.labil.anacarde.application.service.DatabaseService;
import be.labil.anacarde.domain.dto.db.LanguageDto;
import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import be.labil.anacarde.domain.mapper.ExportAuctionMapper;
import be.labil.anacarde.domain.model.*;
import be.labil.anacarde.infrastructure.persistence.*;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import be.labil.anacarde.infrastructure.persistence.view.ExportAuctionRepository;
import be.labil.anacarde.infrastructure.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Classe de base pour les tests d'intégration qui nécessitent des utilisateurs et des rôles de test
 * en base de données.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisMocks.class)
public abstract class AbstractIntegrationTest {

	protected @Autowired JwtUtil jwtUtil;
	protected @Autowired UserRepository userRepository;
	protected @Autowired LanguageRepository languageRepository;
	protected @Autowired StoreRepository storeRepository;
	protected @Autowired ProductRepository productRepository;
	protected @Autowired AuctionRepository auctionRepository;
	protected @Autowired AuctionStrategyRepository auctionStrategyRepository;
	protected @Autowired BidRepository bidRepository;
	protected @Autowired TradeStatusRepository tradeStatusRepository;
	protected @Autowired UserDetailsService userDetailsService;
	protected @Autowired FieldRepository fieldRepository;
	protected @Autowired CooperativeRepository cooperativeRepository;
	protected @Autowired RegionRepository regionRepository;
	protected @Autowired CityRepository cityRepository;
	protected @Autowired DocumentRepository documentRepository;
	protected @Autowired QualityRepository qualityRepository;
	protected @Autowired QualityTypeRepository qualityTypeRepository;
	protected @Autowired ContractOfferRepository contractOfferRepository;
	protected @Autowired QualityControlRepository qualityControlRepository;
	protected @Autowired DatabaseService databaseService;
	protected @Autowired ExportAuctionRepository exportAuctionRepository;
	protected @Autowired ExportAuctionMapper exportAuctionMapper;
	protected @Autowired GlobalSettingsRepository globalSettingsRepository;
	protected @Autowired NewsRepository newsRepository;
	protected @Autowired NewsCategoryRepository newsCategoryRepository;

	private Language mainLanguage;
	private User mainTestUser;
	private User secondTestUser;
	private User producerTestUser;
	private User secondTestProducer;
	private User transformerTestUser;
	private User exporterTestUser;
	private User mainTestCarrier;
	private Store mainTestStore;
	private Product testHarvestProduct;
	private Product testTransformedProduct;
	private Auction testAuction;
	private Auction testAuctionByTransformer;
	private AuctionStrategy testAuctionStrategy;
	private Bid testBid;
	private TradeStatus testTradeStatus;
	private TradeStatus rejectedTradeStatus;
	private Field mainTestField;
	private Cooperative mainTestCooperative;
	private Region mainTestRegion;
	private City mainTestCity;
	private Address mainAddress;
	private Document mainTestDocument;
	private Quality mainTestQuality;
	private QualityType mainQualityType;
	private ContractOffer mainTestContractOffer;
	private QualityControl mainTestQualityControl;
	private NewsCategory mainTestNewsCategory;
	private News mainTestNews;

	@Getter
	private Cookie jwtCookie;
	protected @Autowired WebApplicationContext wac;
	protected MockMvc mockMvc;

	/**
	 * Ligne « principale » de la vue v_auction_bid_analysis (celle correspondant à l’enchère créée
	 * dans initUserDatabase()).
	 */
	protected ExportAuctionDto getMainExportAuction() {
		return exportAuctionRepository.findByAuctionId(getTestAuction().getId())
				.map(exportAuctionMapper::toDto).orElseThrow(() -> new IllegalStateException(
						"aucune ligne dans la vue pour auction_id=" + getTestAuction().getId()));
	}

	@BeforeEach
	public void setUp() {
		initUserDatabase();
		initJwtCookie();

		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity())
				// on crée une "requête par défaut" qui porte jwt+csrf
				.defaultRequest(get("/").cookie(getJwtCookie()).with(csrf())).build();
	}

	@AfterEach
	public void tearDown() throws IOException {
		databaseService.dropDatabase();
	}

	/**
	 * Ajoute automatiquement le cookie JWT et un token CSRF valide à la requête.
	 */
	protected RequestPostProcessor jwtAndCsrf() {
		return request -> {
			request.setCookies(getJwtCookie());
			csrf().postProcessRequest(request);
			return request;
		};
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
			throw new IllegalStateException("Transformer de test non initialisé");
		}
		return transformerTestUser;
	}

	/**
	 * Renvoie un utilisateur exportateur de test.
	 */
	public User getExporterTestUser() {
		if (exporterTestUser == null) {
			throw new IllegalStateException("Exporter de test non initialisé");
		}
		return exporterTestUser;
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
	 * Renvoie la langue principale de test sous forme de DTO.
	 */
	public LanguageDto getMainLanguageDto() {
		return LanguageDto.builder().id(getMainLanguage().getId()).code(getMainLanguage().getCode())
				.name(getMainLanguage().getName()).build();
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

	public Auction getTestAuctionByTransformer() {
		if (testAuctionByTransformer == null) {
			throw new IllegalStateException("Enchère de test non initialisée");
		}
		return testAuctionByTransformer;
	}

	public Bid getTestBid() {
		if (testBid == null) {
			throw new IllegalStateException("Enchère de test non initialisée");
		}
		return testBid;
	}

	public TradeStatus getTestTradeStatus() {
		if (testTradeStatus == null) {
			throw new IllegalStateException("Statut de trade non initialisé");
		}
		return testTradeStatus;
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

	/**
	 * Renvoie un Type de Qualité de test.
	 */
	public QualityType getMainTestQualityType() {
		if (mainQualityType == null) {
			throw new IllegalStateException("Qualité de test non initialisée");
		}
		return mainQualityType;
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

	public NewsCategory getMainTestNewsCategory() {
		if (mainTestNewsCategory == null) {
			throw new IllegalStateException("News category de test non initialisée");
		}
		return mainTestNewsCategory;
	}

	public News getMainTestNews() {
		if (mainTestNews == null) {
			throw new IllegalStateException("News de test non initialisée");
		}
		return mainTestNews;
	}

	public TradeStatus getTradeStatusRejected() {
		if (this.rejectedTradeStatus == null) {
			throw new IllegalStateException("Statut de trade non initialisé");
		}
		return this.rejectedTradeStatus;
	}

	/**
	 * Initialise la base de données des utilisateurs avec deux utilisateurs de test et les rôles
	 * associés.
	 */
	public void initUserDatabase() {

		Language language = Language.builder().name("Français").code("fr").build();
		mainLanguage = languageRepository.save(language);

		// A simple region
		Region region = Region.builder().name("sud").id(1).build();
		mainTestRegion = regionRepository.save(region);

		City city = City.builder().name("sud city").id(1).region(mainTestRegion).build();
		mainTestCity = cityRepository.save(city);
		Point storeLocation = new GeometryFactory().createPoint(new Coordinate(3.3522, 8.8566));
		mainAddress = Address.builder().street("Rue de la paix").city(mainTestCity)
				.region(mainTestRegion).location(storeLocation).build();

		User user1 = Admin.builder().firstName("John").lastName("Doe").email("user@example.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").address(mainAddress)
				.registrationDate(LocalDateTime.now()).phone("+2290197000000")
				.language(mainLanguage).enabled(true).build();

		User user2 = Admin.builder().firstName("Foo").lastName("Bar").email("foo@bar.com")
				.password("$2a$10$abcdefghijklmnopqrstuv1234567890AB").address(mainAddress)
				.registrationDate(LocalDateTime.now()).phone("+2290197000001")
				.language(mainLanguage).enabled(true).build();

		Producer producer = Producer.builder().firstName("Bruce").lastName("Banner")
				.email("bruce@hulk.com").password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
				.address(mainAddress).registrationDate(LocalDateTime.now()).phone("+2290197000002")
				.language(mainLanguage).enabled(true).agriculturalIdentifier("111-222-333").build();

		User producer2 = Producer.builder().firstName("Steve").lastName("Rogers")
				.email("steve@captain.com").password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
				.address(mainAddress).registrationDate(LocalDateTime.now()).phone("+2290197000003")
				.language(mainLanguage).enabled(true).agriculturalIdentifier("000-111-222").build();

		Trader transformer = Transformer.builder().firstName("Scott").lastName("Summers")
				.email("scott@xmen.com").password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
				.address(mainAddress).registrationDate(LocalDateTime.now()).phone("+2290197000004")
				.language(mainLanguage).enabled(true).build();

		User qualityInspector = QualityInspector.builder().firstName("Inspector").lastName("Best")
				.email("inspector@best.com").password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
				.address(mainAddress).phone("+2290197000005").registrationDate(LocalDateTime.now())
				.language(mainLanguage).enabled(true).build();

		User carrier = Carrier.builder().firstName("Pierre").lastName("Verse")
				.email("pierre@verse.com").password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
				.address(mainAddress).registrationDate(LocalDateTime.now()).phone("+2290197000006")
				.language(mainLanguage).enabled(true).pricePerKm(100d).radius(30d).build();

		User exporter = Exporter.builder().firstName("Paris").lastName("Verse")
				.email("exporter@verse.com").password("$2a$10$abcdefghijklmnopqrstuv1234567890AB")
				.address(mainAddress).registrationDate(LocalDateTime.now()).phone("+2290197000029")
				.language(mainLanguage).enabled(true).build();

		// Ne plus assigner de rôles - on utilise l'héritage
		mainTestUser = userRepository.save(user1);
		secondTestUser = userRepository.save(user2);
		producerTestUser = userRepository.save(producer);
		secondTestProducer = userRepository.save(producer2);
		transformerTestUser = userRepository.save(transformer);
		mainTestCarrier = userRepository.save(carrier);
		qualityInspector = userRepository.save(qualityInspector);
		exporterTestUser = userRepository.save(exporter);

		Store store = Store.builder().name("Nassara").address(mainAddress).user(mainTestUser)
				.build();
		mainTestStore = storeRepository.save(store);

		// Fields
		// A field binded to main producer
		Point pointField = new GeometryFactory().createPoint(new Coordinate(2.3522, 48.8566));
		Field field = Field.builder().producer((Producer) producerTestUser).identifier("FIELD-001")
				.address(mainAddress).build();

		mainTestField = fieldRepository.save(field);

		// A field to another producer
		Point pointField2 = new GeometryFactory().createPoint(new Coordinate(1.198, 10.300));
		Field field2 = Field.builder().producer((Producer) producerTestUser).identifier("FIELD-002")
				.address(mainAddress).build();
		fieldRepository.save(field2);

		// Quality Type
		QualityType qualityType = QualityType.builder().name("Amande").build();
		mainQualityType = qualityTypeRepository.save(qualityType);

		// A quality
		Quality quality = Quality.builder().name("WW160").qualityType(qualityType).build();
		mainTestQuality = qualityRepository.save(quality);

		// A document with a qualityInspector
		Document document = Document.builder().extension("text").contentType("text/plain")
				.originalFilename("metaBlop.txt").size(120).storagePath("/storage")
				.uploadDate(LocalDateTime.now()).user(qualityInspector).build();
		mainTestDocument = documentRepository.save(document);

		// A quality control
		QualityControl qualityControl = QualityControl.builder().identifier("QC-001")
				.controlDate(LocalDateTime.of(2025, 4, 7, 10, 0)).granularity(0.5f).korTest(0.8f)
				.humidity(12.5f).qualityInspector((QualityInspector) qualityInspector)
				.quality(quality).build();
		mainTestQualityControl = qualityControlRepository.save(qualityControl);

		QualityControl qualityControl2 = QualityControl.builder().identifier("QC-002")
				.controlDate(LocalDateTime.of(2025, 6, 6, 6, 0)).granularity(0.5f).korTest(0.8f)
				.humidity(12.5f).qualityInspector((QualityInspector) qualityInspector)
				.quality(quality).build();
		qualityControlRepository.save(qualityControl2);

		QualityControl qualityControl3 = QualityControl.builder().identifier("QC-003")
				.controlDate(LocalDateTime.of(2025, 6, 6, 6, 0)).granularity(0.5f).korTest(0.8f)
				.humidity(12.5f).qualityInspector((QualityInspector) qualityInspector)
				.quality(quality).build();
		qualityControlRepository.save(qualityControl3);

		// A transformed product
		TransformedProduct productTransform = TransformedProduct.builder()
				.transformer((Transformer) transformerTestUser).store(mainTestStore)
				.deliveryDate(LocalDateTime.now()).identifier("XYZ").weightKg(2000.0)
				.qualityControl(qualityControl3).build();
		testTransformedProduct = productRepository.save(productTransform);

		// A harvest product
		HarvestProduct productHarvest = HarvestProduct.builder()
				.producer((Producer) producerTestUser).store(mainTestStore)
				.deliveryDate(LocalDateTime.now()).weightKg(2000.0).field(mainTestField)
				.qualityControl(qualityControl2)
				.transformedProduct((TransformedProduct) testTransformedProduct).build();
		testHarvestProduct = productRepository.save(productHarvest);

		AuctionStrategy strategy = AuctionStrategy.builder().name("Meilleure offre").build();
		testAuctionStrategy = auctionStrategyRepository.save(strategy);

		AuctionStrategy strategyBid = AuctionStrategy.builder().name("Enchères montantes").build();
		auctionStrategyRepository.save(strategyBid);

		AuctionStrategy strategyFixedPrice = AuctionStrategy.builder().name("Prix fixe").build();
		auctionStrategyRepository.save(strategyFixedPrice);

		GlobalSettings globalSettings = new GlobalSettings();
		globalSettings.setDefaultStrategy(testAuctionStrategy);
		globalSettingsRepository.save(globalSettings);

		TradeStatus tradeStatusOpen = TradeStatus.builder().name("Ouvert").build();
		testTradeStatus = tradeStatusRepository.save(tradeStatusOpen);

		TradeStatus tradeStatusAccepted = TradeStatus.builder().name("Accepté").build();
		tradeStatusRepository.save(tradeStatusAccepted);

		TradeStatus tradeStatusRejected = TradeStatus.builder().name("Refusé").build();
		rejectedTradeStatus = tradeStatusRepository.save(tradeStatusRejected);

		TradeStatus tradeStatusExpired = TradeStatus.builder().name("Expiré").build();
		tradeStatusRepository.save(tradeStatusExpired);

		AuctionOptions auctionOptions = new AuctionOptions();
		auctionOptions.setStrategy(testAuctionStrategy);
		auctionOptions.setFixedPriceKg(150.);
		auctionOptions.setMaxPriceKg(300.);
		auctionOptions.setMinPriceKg(80.);
		auctionOptions.setBuyNowPrice(250.);
		auctionOptions.setShowPublic(true);

		// An auction with a harvest product
		Auction auction = Auction.builder().price(500.0).productQuantity(10).active(true)
				.creationDate(LocalDateTime.now()).expirationDate(LocalDateTime.now())
				.product(productHarvest).options(auctionOptions).trader(producer)
				.status(tradeStatusOpen).build();
		testAuction = auctionRepository.save(auction);

		// An auction with a transformed product
		Auction auction2 = Auction.builder().price(10000.0).productQuantity(1000).active(true)
				.creationDate(LocalDateTime.of(2025, 1, 15, 0, 0))
				.expirationDate(LocalDateTime.of(2025, 2, 15, 0, 0)).product(productTransform)
				.options(auctionOptions).trader(transformer).status(tradeStatusOpen).build();
		auctionRepository.save(auction2);
		testAuctionByTransformer = auction2;

		auctionRepository.overrideCreationDateNative(auction2.getId(),
				LocalDateTime.of(2025, 1, 15, 0, 0));

		// An auction from another user
		Auction auction3 = Auction.builder().price(777.0).productQuantity(777).active(true)
				.creationDate(LocalDateTime.now()).expirationDate(LocalDateTime.now())
				.product(productTransform).options(auctionOptions).trader(transformer)
				.status(tradeStatusOpen).build();
		auctionRepository.save(auction3);

		// A "deleted" auction (= inactive)
		Auction auction4 = Auction.builder().price(999.0).productQuantity(999).active(false)
				.creationDate(LocalDateTime.now()).expirationDate(LocalDateTime.now())
				.product(productTransform).options(auctionOptions).trader(producer)
				.status(tradeStatusOpen).build();
		auctionRepository.save(auction4);

		// An accepted bid on an auction
		Bid bid = Bid.builder().amount(new BigDecimal("10.0")).creationDate(LocalDateTime.now())
				.auctionId(testAuction.getId()).trader(transformer).status(tradeStatusAccepted)
				.build();
		testBid = bidRepository.save(bid);

		// A pending bid on a different auction
		Bid bid2 = Bid.builder().amount(new BigDecimal("500.0")).creationDate(LocalDateTime.now())
				.auctionId(auction2.getId()).trader(transformer).status(tradeStatusOpen).build();
		bidRepository.save(bid2);

		// A cooperative who has for president 'producer'
		Cooperative cooperative = Cooperative.builder().name("Coopérative Agricole de Parakou")
				.president(producer).creationDate(LocalDateTime.of(2025, 4, 7, 12, 0)).build();
		mainTestCooperative = cooperativeRepository.save(cooperative);

		producer.setCooperative(mainTestCooperative);
		producerTestUser = userRepository.save(producer);

		// A document with a qualityInspector
		Document document2 = Document.builder().contentType("QUALITY")
				.originalFilename("attestation.pdf").size(212).extension("PDF")
				.storagePath("/storage").user(qualityInspector).uploadDate(LocalDateTime.now())
				.build();
		mainTestDocument = documentRepository.save(document2);

		// A quality
		Quality quality2 = Quality.builder().name("WW160").qualityType(qualityType).build();
		mainTestQuality = qualityRepository.save(quality2);

		// A contract
		ContractOffer contractOffer = ContractOffer.builder().status("Accepté")
				.pricePerKg(new BigDecimal("20.0")).creationDate(LocalDateTime.now())
				.endDate(LocalDateTime.now().plusDays(1)).seller(producer).buyer(transformer)
				.quality(quality).build();
		mainTestContractOffer = contractOfferRepository.save(contractOffer);

		// News Category
		NewsCategory newsCategory = NewsCategory.builder().name("General")
				.description("Catégorie générale").build();
		mainTestNewsCategory = newsCategoryRepository.save(newsCategory);

		// News Article
		News news = News.builder().title("Welcome to Anacarde Blog")
				.content("This is the first news article on our platform.")
				.publicationDate(LocalDateTime.now().minusDays(1)).category(mainTestNewsCategory)
				.authorName("Test Author").build();
		mainTestNews = newsRepository.save(news);
	}
	/**
	 * Génère un cookie JWT HTTP-only en utilisant les détails de l'utilisateur admin de test.
	 */
	protected void initJwtCookie() {
		UserDetails userDetails = userDetailsService
				.loadUserByUsername(getMainTestUser().getEmail());
		String token = jwtUtil.generateToken(userDetails);
		jwtCookie = new Cookie("jwt", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
	}

	/**
	 * RequestPostProcessor pour un producer.
	 */
	protected RequestPostProcessor jwtProducer() {
		return request -> {
			UserDetails userDetails = userDetailsService
					.loadUserByUsername(getProducerTestUser().getEmail());
			String token = jwtUtil.generateToken(userDetails);
			jakarta.servlet.http.Cookie userCookie = new jakarta.servlet.http.Cookie("jwt", token);
			userCookie.setHttpOnly(true);
			userCookie.setPath("/");
			request.setCookies(userCookie);
			return request;
		};
	}

	/**
	 * RequestPostProcessor pour un transformer.
	 */
	protected RequestPostProcessor jwtTransformer() {
		return request -> {
			UserDetails userDetails = userDetailsService
					.loadUserByUsername(getTransformerTestUser().getEmail());
			String token = jwtUtil.generateToken(userDetails);
			jakarta.servlet.http.Cookie userCookie = new jakarta.servlet.http.Cookie("jwt", token);
			userCookie.setHttpOnly(true);
			userCookie.setPath("/");
			request.setCookies(userCookie);
			return request;
		};
	}

	/**
	 * RequestPostProcessor pour un carrier.
	 */
	protected RequestPostProcessor jwtCarrier() {
		return request -> {
			UserDetails userDetails = userDetailsService
					.loadUserByUsername(getMainTestCarrier().getEmail());
			String token = jwtUtil.generateToken(userDetails);
			jakarta.servlet.http.Cookie userCookie = new jakarta.servlet.http.Cookie("jwt", token);
			userCookie.setHttpOnly(true);
			userCookie.setPath("/");
			request.setCookies(userCookie);
			return request;
		};
	}

	/**
	 * RequestPostProcessor pour un exporter.
	 */
	protected RequestPostProcessor jwtExporter() {
		return request -> {
			UserDetails userDetails = userDetailsService
					.loadUserByUsername(getExporterTestUser().getEmail());
			String token = jwtUtil.generateToken(userDetails);
			jakarta.servlet.http.Cookie userCookie = new jakarta.servlet.http.Cookie("jwt", token);
			userCookie.setHttpOnly(true);
			userCookie.setPath("/");
			request.setCookies(userCookie);
			return request;
		};
	}
}
