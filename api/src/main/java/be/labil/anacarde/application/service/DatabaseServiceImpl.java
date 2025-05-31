package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.*;
import be.labil.anacarde.domain.dto.db.product.HarvestProductDto;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.db.product.TransformedProductDto;
import be.labil.anacarde.domain.dto.db.user.*;
import be.labil.anacarde.domain.dto.write.*;
import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.domain.dto.write.user.create.*;
import be.labil.anacarde.domain.mapper.DocumentMapper;
import be.labil.anacarde.domain.mapper.QualityControlMapper;
import be.labil.anacarde.domain.mapper.QualityMapper;
import be.labil.anacarde.domain.model.*;
import be.labil.anacarde.infrastructure.importdata.RegionCityImportService;
import be.labil.anacarde.infrastructure.persistence.*;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.commons.io.IOUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DatabaseServiceImpl implements DatabaseService {

	// --- Configuration Constants ---
	private static final int TARGET_YEAR = 2025;
	private static final int NUM_PENDING_PRODUCERS = 10;
	private static final int NUM_PRODUCERS = 120;
	private static final int NUM_TRANSFORMERS = 30;
	private static final int NUM_EXPORTERS = 10;
	private static final int NUM_CARRIERS = 10;
	private static final int NUM_QUALITY_INSPECTORS = 10;
	private static final int NUM_ADMINS = 3;
	private static final int NUM_COOPERATIVES = 15;
	private static final int NUM_STORES = NUM_PRODUCERS + NUM_TRANSFORMERS;
	private static final int MIN_FIELDS_PER_PRODUCER = 1;
	private static final int MAX_FIELDS_PER_PRODUCER = 5;
	private static final int MIN_PRODUCTS_PER_PRODUCER = 3;
	private static final int MAX_PRODUCTS_PER_PRODUCER = 10;
	private static final int MIN_PRODUCTS_PER_TRANSFORMER = 5;
	private static final int MAX_PRODUCTS_PER_TRANSFORMER = 15;
	private static final int MIN_BIDS_PER_FINISHED_AUCTION = 0;
	private static final int MAX_BIDS_PER_FINISHED_AUCTION = 10;
	private static final int MIN_AUCTION_DURATION_DAYS = 15;
	private static final int MAX_AUCTION_DURATION_DAYS = 60;
	private static final int MIN_HARVEST_PRODUCT_PER_TRANSFORMED_PRODUCT = 1;
	private static final int MAX_HARVEST_PRODUCT_PER_TRANSFORMED_PRODUCT = 5;
	private static final int MIN_PRODUCT_50KG_BAG = 1;
	private static final int MAX_PRODUCT_50KG_BAG = 100;
	private static final int MIN_AUCTION_INCREMENT = 1; // kCFA
	private static final int MAX_AUCTION_INCREMENT = 5; // kCFA
	private static final String DEFAULT_PASSWORD = "password";

	private static final Map<Class<? extends UserCreateDto>, String> DEFAULT_EMAILS = Map.of(
			AdminCreateDto.class, "admin@example.com", ProducerCreateDto.class,
			"producer@example.com", TransformerCreateDto.class, "transformer@example.com",
			ExporterCreateDto.class, "exporter@example.com", CarrierCreateDto.class,
			"carrier@example.com", QualityInspectorCreateDto.class,
			"quality_inspector@example.com");

	// --- Injected Dependencies (Final with Lombok RequiredArgsConstructor) ---
	private final UserRepository userRepository;
	private final LanguageRepository languageRepository;
	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;
	private final AuctionRepository auctionRepository;
	private final AuctionStrategyRepository auctionStrategyRepository;
	private final BidRepository bidRepository;
	private final TradeStatusRepository tradeStatusRepository;
	private final FieldRepository fieldRepository;
	private final CooperativeRepository cooperativeRepository;
	private final DocumentRepository documentRepository;
	private final QualityRepository qualityRepository;
	private final ContractOfferRepository contractOfferRepository;
	private final QualityControlRepository qualityControlRepository;
	private final CityRepository cityRepository;
	private final RegionRepository regionRepository;
	private final GlobalSettingsRepository globalSettingsRepository;
	private final NewsRepository newsRepository;
	private final NewsCategoryRepository newsCategoryRepository;

	private final StoreService storeService;
	private final ProductService productService;
	private final UserService userService;
	private final AuctionService auctionService;
	private final TradeStatusService tradeStatusService;
	private final BidService bidService;
	private final LanguageService languageService;
	private final CooperativeService cooperativeService;
	private final AuctionStrategyService auctionStrategyService;
	private final FieldService fieldService;
	private final RegionCityImportService regionCityImportService;
	private final GlobalSettingsService globalSettingsService;

	private final EntityManager entityManager;

	// --- Internal State ---
	private final Faker faker = new Faker(Locale.of("fr")); // Use French Faker locale
	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
	private final Random random = new Random();
	private final QualityTypeRepository qualityTypeRepository;
	private final DocumentMapper documentMapper;
	private final QualityControlMapper qualityControlMapper;
	private final QualityMapper qualityMapper;
	private LocalDateTime generationTime;
	private final Set<String> generatedEmails = new HashSet<>();
	private final Set<String> generatedPhones = new HashSet<>();
	private final List<AuctionDto> createdAuctions = new ArrayList<>();
	private static final double[] regionWeights = {0.006, // ID 1
			0.158, // ID 2 (Atacora 15.8 %)
			0.006, // ID 3
			0.259, // ID 4 (Borgou 25.9 %)
			0.101, // ID 5 (Collines 10.1 %)
			0.045, // ID 6 (Autres 4.5 %)
			0.395, // ID 7 (Donga 39.5 %)
			0.006, // ID 8
			0.006, // ID 9
			0.006, // ID 10
			0.006, // ID 11
			0.006 // ID 12 (Zou 0.6 %)
	};
	// clé = qualité, valeur = [prixMin, prixMax] en FCFA/kg
	private static final Map<String, double[]> cashewPriceRanges = Map.of("Grade I",
			new double[]{600.0, 800.0}, "Grade II", new double[]{500.0, 700.0}, "Grade III",
			new double[]{400.0, 600.0}, "Hors normes", new double[]{300.0, 500.0});

	private LanguageDto langFr;
	private LanguageDto langEn;
	private LanguageDto langAr;
	private LanguageDto langEs;
	private LanguageDto langZhCn;
	private TradeStatusDto statusOpen;
	private TradeStatusDto statusAccepted;
	private TradeStatusDto statusExpired;
	private TradeStatusDto statusConcluded; // Assuming 'Conclu' might be another finished status
	private TradeStatusDto statusRejected;
	private TradeStatusDto statusCancelled;
	private AuctionStrategyDto strategyOffer;

	private List<Region> regions;
	private List<City> cities;
	private List<ProducerDetailDto> createdProducers = new ArrayList<>();
	private List<TransformerDetailDto> createdTransformers = new ArrayList<>();
	private List<ExporterDetailDto> createdExporters = new ArrayList<>();
	private List<AdminDetailDto> createdAdmins = new ArrayList<>();
	private List<CarrierDetailDto> createdCarriers = new ArrayList<>();
	private List<QualityInspectorDetailDto> createdQualityInspectors = new ArrayList<>();
	private List<StoreDetailDto> createdStores = new ArrayList<>();
	private List<CooperativeDto> createdCooperatives = new ArrayList<>();
	private Map<Integer, List<FieldDto>> producerFieldsMap = new HashMap<>();
	private List<ProductDto> createdProducts = new ArrayList<>();
	private List<QualityDto> anacardeQualities = new ArrayList<>();
	private List<QualityDto> amandeQualities = new ArrayList<>();
	private final Map<Integer, List<Integer>> transformerWonHarvestProductsMap = new HashMap<>();
	private final Set<Integer> auctionsWithBids = new HashSet<>();

	private final EntityManager em;
	private final ResourceLoader resourceLoader;
	private static final String CREATE_VIEW_EXPORT_AUCTION = "classpath:sql/export_auctions.sql";
	private static final String CREATE_VIEW_DASHBOARD_CARDS = "classpath:sql/dashboard_cards.sql";
	private static final String CREATE_VIEW_DASHBOARD_GRAPHIC = "classpath:sql/dashboard_graphic.sql";

	@Override
	public boolean isInitialized() {
		return userRepository.count() > 0;
	}

	@Override
	public void dropDatabase() {
		userRepository.findAll().forEach(p -> {
			if (p instanceof Producer c) {
				c.setCooperative(null);
				userRepository.save(c);
			}
		});
		userRepository.flush();
		globalSettingsRepository.deleteAllInBatch();
		cooperativeRepository.deleteAllInBatch();
		contractOfferRepository.deleteAllInBatch();
		bidRepository.deleteAllInBatch();
		auctionRepository.deleteAllInBatch();
		tradeStatusRepository.deleteAllInBatch();
		auctionStrategyRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		documentRepository.deleteAllInBatch();
		qualityControlRepository.deleteAllInBatch();
		qualityRepository.deleteAllInBatch();
		qualityTypeRepository.deleteAllInBatch();
		fieldRepository.deleteAllInBatch();
		storeRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		cityRepository.deleteAllInBatch();
		regionRepository.deleteAllInBatch();
		languageRepository.deleteAllInBatch();
		newsRepository.deleteAllInBatch();
		newsCategoryRepository.deleteAllInBatch();
	}

	@Override
	@Transactional
	public void createDatabase() throws IOException {
		log.info("→ createDatabase() (compatibilité) ←");
		initDatabase();
		initTestData();
		initViews();
	}

	/**
	 * Initialise toutes les données de référence.
	 */
	@Transactional
	public void initDatabase() throws IOException {
		generationTime = LocalDateTime.now();
		log.info("===== initDatabase() – données de référence =====");

		createBaseLookups();
		createRegionsAndCities();
		initCategories();
		entityManager.flush();
		log.info("Référentiel terminé ✔");
	}

	/**
	 * Injecte des données factices pour la démonstration / tests. Suppose que initDatabase() a déjà
	 * été invoquée.
	 */
	@Transactional
	public void initTestData() {
		generationTime = LocalDateTime.now();
		log.info("===== initTestData() – données de test =====");

		createUsers();
		createCooperativesAndAssignMembers();
		createStores();
		createFields();

		createHarvestProducts();
		createAuctions(false);
		createBidsForFinishedAuctions();

		createTransformedProducts();
		createAuctions(true);
		createBidsForFinishedAuctions();

		createArticles();

		entityManager.flush();
		log.info("Données de test générées ✔");
	}

	private void initViews() throws IOException {
		executeViewScript(CREATE_VIEW_EXPORT_AUCTION);
		executeViewScript(CREATE_VIEW_DASHBOARD_CARDS);
		executeViewScript(CREATE_VIEW_DASHBOARD_GRAPHIC);
	}

	private void executeViewScript(String location) throws IOException {
		Resource resource = resourceLoader.getResource(location);
		String sql = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
		em.createNativeQuery(sql).executeUpdate();
	}

	private void createBaseLookups() {
		log.debug("Creating base lookups...");
		// Languages
		langFr = languageService
				.createLanguage(LanguageDto.builder().name("Français").code("fr").build());
		langEn = languageService
				.createLanguage(LanguageDto.builder().name("English").code("en").build());
		langAr = languageService
				.createLanguage(LanguageDto.builder().name("العربية").code("ar").build());
		langEs = languageService
				.createLanguage(LanguageDto.builder().name("Español").code("es").build());
		langZhCn = languageService
				.createLanguage(LanguageDto.builder().name("简体中文").code("zh-CN").build());

		// Trade Statuses
		statusOpen = tradeStatusService.createTradeStatus(createTradeStatusDto("Ouvert"));
		statusAccepted = tradeStatusService.createTradeStatus(createTradeStatusDto("Accepté"));
		statusExpired = tradeStatusService.createTradeStatus(createTradeStatusDto("Expiré"));
		statusConcluded = tradeStatusService.createTradeStatus(createTradeStatusDto("Conclu"));
		statusRejected = tradeStatusService.createTradeStatus(createTradeStatusDto("Refusé"));
		statusCancelled = tradeStatusService.createTradeStatus(createTradeStatusDto("Annulé"));

		// Auction Strategies
		AuctionStrategyDto dto = new AuctionStrategyDto();
		dto.setName("Meilleure offre");
		strategyOffer = auctionStrategyService.createAuctionStrategy(dto);
		AuctionStrategyDto dto1 = new AuctionStrategyDto();
		dto1.setName("Enchères montantes");
		AuctionStrategyDto strategyBid = auctionStrategyService.createAuctionStrategy(dto1);
		AuctionStrategyDto dtoFixedPrice = new AuctionStrategyDto();
		dtoFixedPrice.setName("Prix fixe");
		auctionStrategyService.createAuctionStrategy(dtoFixedPrice);

		GlobalSettingsUpdateDto globalSettingsUpdateDto = new GlobalSettingsUpdateDto();
		globalSettingsUpdateDto.setDefaultStrategyId(strategyOffer.getId());
		globalSettingsUpdateDto.setForceBetterBids(false);
		globalSettingsUpdateDto.setDefaultMinPriceKg(BigDecimal.valueOf(10));
		globalSettingsUpdateDto.setDefaultMaxPriceKg(BigDecimal.valueOf(1000000));
		globalSettingsUpdateDto.setMinIncrement(0);
		globalSettingsUpdateDto.setShowOnlyActive(false);
		globalSettingsService.updateGlobalSettings(globalSettingsUpdateDto);
		log.debug("Base lookups created.");

		// create QualityTypes and Qualities
		QualityType anacardeType = qualityTypeRepository.save(new QualityType("Harvest"));
		QualityType amandeType = qualityTypeRepository.save(new QualityType("Transformed"));

		List<String> anacardeCategory = List.of("Grade I", "Grade II", "Grade III", "Hors normes");

		for (String name : anacardeCategory) {
			Quality q = new Quality();
			q.setName(name);
			q.setQualityType(anacardeType);
			anacardeQualities.add(qualityMapper.toDto(qualityRepository.save(q)));
		}

		List<String> amandeCategories = List.of("WW", "SW", "DW");
		List<String> amandeCalibres = List.of("180", "210", "240", "280", "320", "400", "450",
				"500");

		for (String cat : amandeCategories) {
			for (String calibre : amandeCalibres) {
				String name = cat + calibre;
				Quality q = new Quality();
				q.setName(name);
				q.setQualityType(amandeType);
				amandeQualities.add(qualityMapper.toDto(qualityRepository.save(q)));
			}
		}
		// Les autres styles et morceaux sans calibre
		List<String> otherAmandeQualities = List.of("SWP", "LWP", "FB", "SB", "FS", "SS", "SP",
				"SSP", "SPS", "DP", "BB", "SSW");
		for (String name : otherAmandeQualities) {
			Quality q = new Quality();
			q.setName(name);
			q.setQualityType(amandeType);
			amandeQualities.add(qualityMapper.toDto(qualityRepository.save(q)));
		}
	}

	private void createRegionsAndCities() throws IOException {

		regions = List.of();
		cities = List.of();
		try {
			Map<Integer, Region> regs = regionCityImportService.importRegions();
			regions = regs.values().stream().toList();
			cities = regionCityImportService.importCities("/data/cities.json", regs);
			entityManager.flush();
		} catch (Exception e) {
			System.out
					.println("Erreur lors de la création des régions et villes: " + e.getMessage());
		}
	}

	private void initCategories() {

		Map<String, String> defaults = Map.of("Alertes terrain",
				"Notifications en temps réel des producteurs sur les aléas (maladies, "
						+ "infestations, épisodes climatiques extrêmes) et réponses rapides "
						+ "des experts.",

				"Marché & Prix",
				"Analyses régulières des cours internationaux de la noix de cajou, "
						+ "tendances régionales et volumes échangés pour optimiser vos décisions.",

				"Recherche & Innovations",
				"Vulgarisation des résultats de la recherche scientifique appliquée, "
						+ "études de cas et technologies émergentes validées par les experts.",

				"Formation & Ressources",
				"Guides pratiques et capsules vidéo couvrant les meilleures pratiques "
						+ "culturales, accessibles à tout moment.");

		defaults.forEach((name, desc) -> newsCategoryRepository.findByName(name).orElseGet(() -> {
			log.info("Création de la catégorie « {} »", name);
			return newsCategoryRepository
					.save(NewsCategory.builder().name(name).description(desc).build());
		}));
	}

	private void createUsers() {

		createUsersOfType(ProducerCreateDto.class, this::createRandomPendingProducerDto,
				dto -> (ProducerDetailDto) userService.createUser(dto, null), NUM_PENDING_PRODUCERS,
				false);

		createdAdmins = createUsersOfType(AdminCreateDto.class, this::createRandomAdminDto,
				dto -> (AdminDetailDto) userService.createUser(dto, null), NUM_ADMINS);

		createdProducers = createUsersOfType(ProducerCreateDto.class, this::createRandomProducerDto,
				dto -> (ProducerDetailDto) userService.createUser(dto, null), NUM_PRODUCERS);

		createdTransformers = createUsersOfType(TransformerCreateDto.class,
				this::createRandomTransformerDto,
				dto -> (TransformerDetailDto) userService.createUser(dto, null), NUM_TRANSFORMERS);

		createdExporters = createUsersOfType(ExporterCreateDto.class, this::createRandomExporterDto,
				dto -> (ExporterDetailDto) userService.createUser(dto, null), NUM_EXPORTERS);

		createdCarriers = createUsersOfType(CarrierCreateDto.class, this::createRandomCarrierDto,
				dto -> (CarrierDetailDto) userService.createUser(dto, null), NUM_CARRIERS);

		createdQualityInspectors = createUsersOfType(QualityInspectorCreateDto.class,
				this::createRandomQualityInspectorDto,
				dto -> (QualityInspectorDetailDto) userService.createUser(dto, null),
				NUM_QUALITY_INSPECTORS);

		// Regroupe les listes dans une seule structure
		List<List<? extends UserDetailDto>> userGroups = Arrays.asList(createdAdmins,
				createdProducers, createdTransformers, createdExporters, createdCarriers,
				createdQualityInspectors);

		// Parcourt chaque groupe puis chaque utilisateur
		for (List<? extends UserDetailDto> group : userGroups) {
			for (UserDetailDto user : group) {
				userRepository.overrideCreationDateNative(user.getId(),
						generateRandomDateTimeInPast());
			}
		}

		log.info("Users created.");
	}

	/**
	 * Crée d’abord un utilisateur « par défaut » avec un email fixe, puis d’autres utilisateurs
	 * aléatoires.
	 *
	 * @param dtoClass
	 *            La classe du DTO (pour récupérer l’email fixe)
	 * @param dtoSupplier
	 *            Fournit un nouveau DTO « vide »
	 * @param createMethod
	 *            Appelle userService.createUser et cast le résultat
	 * @param totalCount
	 *            Nombre total d’utilisateurs à créer
	 * @param <U>
	 *            Type du DTO d’entrée
	 * @param <D>
	 *            Type du DTO renvoyé par userService
	 * @return Liste des DTO créés
	 */
	private <U extends UserCreateDto, D> List<D> createUsersOfType(Class<U> dtoClass,
			Supplier<U> dtoSupplier, Function<U, D> createMethod, int totalCount,
			boolean insertDefault) {
		List<D> list = new ArrayList<>(totalCount);

		log.info("Création de {} instances de {}", totalCount, dtoClass.getSimpleName());

		if (insertDefault) {
			U defaultDto = dtoSupplier.get();
			defaultDto.setEmail(DEFAULT_EMAILS.get(dtoClass));
			D defaultUser = createMethod.apply(defaultDto);
			list.add(defaultUser);
		}

		IntStream.range(1, totalCount).forEach(i -> {
			U randomDto = dtoSupplier.get();
			D randomUser = createMethod.apply(randomDto);
			list.add(randomUser);
		});

		return list;
	}

	private <U extends UserCreateDto, D> List<D> createUsersOfType(Class<U> dtoClass,
			Supplier<U> dtoSupplier, Function<U, D> createMethod, int totalCount) {
		return createUsersOfType(dtoClass, dtoSupplier, createMethod, totalCount, true);
	}

	private void createCooperativesAndAssignMembers() {
		if (createdProducers.isEmpty()) {
			log.warn("No producers available to create cooperatives.");
			return;
		}

		log.info("Creating {} Cooperatives...", NUM_COOPERATIVES);
		createdCooperatives = new ArrayList<>();

		List<ProducerDetailDto> presidentsPool = new ArrayList<>(createdProducers);
		List<ProducerDetailDto> membersPool = new ArrayList<>(createdProducers);

		for (int i = 0; i < NUM_COOPERATIVES && !presidentsPool.isEmpty(); i++) {

			ProducerDetailDto presidentDto = presidentsPool
					.remove(random.nextInt(presidentsPool.size()));
			membersPool.remove(presidentDto); // pas re-sélectionné comme membre

			CooperativeUpdateDto coopDto = new CooperativeUpdateDto();
			coopDto.setName(faker.company().name() + " Coopérative");
			coopDto.setCreationDate(generateRandomDateTimeInPast());
			coopDto.setPresidentId(presidentDto.getId());

			CooperativeDto createdCoop = cooperativeService.createCooperative(coopDto);
			cooperativeRepository.overrideCreationDateNative(createdCoop.getId(),
					generateRandomDateTimeInPast());
			createdCooperatives.add(createdCoop);

			// Persist le président
			userRepository.findById(presidentDto.getId()).ifPresent(user -> {
				if (user instanceof Producer p) {
					p.setCooperative(cooperativeRepository.getReferenceById(createdCoop.getId()));
					userRepository.save(p);
				}
			});

			/* ---------- 2) Membres ---------- */
			int maxMembers = membersPool.size() / (NUM_COOPERATIVES - i); // répartition grossière
			int numMembers = random.nextInt(maxMembers + 1); // parfois 0
			Collections.shuffle(membersPool);

			for (int j = 0; j < numMembers; j++) {
				ProducerDetailDto memberDto = membersPool.removeFirst();
				userRepository.findById(memberDto.getId()).ifPresent(user -> {
					if (user instanceof Producer p) {
						p.setCooperative(
								cooperativeRepository.getReferenceById(createdCoop.getId()));
						userRepository.save(p); // <<-- persiste le membre
					}
				});
				// optionnel : aussi mettre à jour le DTO en mémoire
				memberDto.setCooperative(createdCoop);
			}
		}
		log.info("Cooperatives created, presidents and members assigned.");
	}

	private void createStores() {
		if (createdProducers.isEmpty() && createdTransformers.isEmpty()) {
			log.warn("No Producers or Transformers available to manage stores.");
		}
		log.info("Creating {} Stores", NUM_STORES);
		createdStores = new ArrayList<>();
		List<UserDetailDto> potentialManagers = new ArrayList<>();
		potentialManagers.addAll(createdProducers);
		potentialManagers.addAll(createdTransformers);

		for (int i = 0; i < NUM_STORES; i++) {
			UserDetailDto manager = potentialManagers.get(random.nextInt(potentialManagers.size()));
			StoreDetailDto storeDto = new StoreDetailDto();
			storeDto.setName(faker.company().suffix() + " " + faker.address().city() + " Dépôt");
			storeDto.setAddress(createVariationAddress(manager.getAddress()));
			storeDto.setUserId(manager.getId());
			try {
				createdStores.add(storeService.createStore(storeDto));
			} catch (Exception e) {
				log.error("Failed to create store for manager {}: {}", manager.getId(),
						e.getMessage());
			}
		}
		log.info("Stores created.");
	}

	private void createFields() {
		if (createdProducers.isEmpty()) {
			log.warn("No producers available to create fields.");
			return;
		}
		log.info("Creating Fields for Producers...");
		producerFieldsMap = new HashMap<>();
		for (ProducerDetailDto producer : createdProducers) {
			int numFields = random.nextInt(MAX_FIELDS_PER_PRODUCER - MIN_FIELDS_PER_PRODUCER + 1)
					+ MIN_FIELDS_PER_PRODUCER;
			List<FieldDto> fieldsForProducer = new ArrayList<>();
			for (int i = 0; i < numFields; i++) {
				FieldDto fieldDto = new FieldDto();
				fieldDto.setIdentifier("FIELD-" + faker.number().digits(10));
				fieldDto.setAddress(createVariationAddress(producer.getAddress()));
				fieldDto.setProducer(producer);
				try {
					fieldsForProducer.add(fieldService.createField(fieldDto));
				} catch (Exception e) {
					log.error("Failed to create field for producer {}: {}", producer.getId(),
							e.getMessage());
				}
			}
			producerFieldsMap.put(producer.getId(), fieldsForProducer);
		}
		log.info("Fields created.");
	}

	private void createHarvestProducts() {
		if (createdProducers.isEmpty()) {
			log.warn("No producers to create harvest products.");
			return;
		}
		log.info("Creating Harvest Products…");

		for (ProducerDetailDto producer : createdProducers) {
			List<FieldDto> fields = producerFieldsMap.getOrDefault(producer.getId(),
					Collections.emptyList());
			if (fields.isEmpty() || createdStores.isEmpty()) continue;

			int numProducts = faker.number().numberBetween(MIN_PRODUCTS_PER_PRODUCER,
					MAX_PRODUCTS_PER_PRODUCER + 1);

			for (int i = 0; i < numProducts; i++) {
				StoreDetailDto store = createdStores.get(random.nextInt(createdStores.size()));
				FieldDto field = fields.get(random.nextInt(fields.size()));
				QualityInspectorDetailDto inspector = createdQualityInspectors
						.get(random.nextInt(createdQualityInspectors.size()));
				QualityDto quality = anacardeQualities
						.get(random.nextInt(anacardeQualities.size()));

				DocumentUpdateDto docDto = new DocumentUpdateDto();
				String[] extensions = {"pdf", "docx", "xlsx", "jpg", "png"};
				docDto.setExtension(faker.options().option(extensions));
				docDto.setContentType("application/" + docDto.getExtension());
				docDto.setOriginalFilename(
						faker.file().fileName(null, null, docDto.getExtension(), null));
				docDto.setSize(faker.number().numberBetween(128, 4097));
				docDto.setStoragePath("/documents/2025/harvest_" + faker.number().digits(10) + "."
						+ docDto.getExtension());
				docDto.setUploadDate(generateRandomDateTimeInPast());
				docDto.setUserId(producer.getId());
				Document document = documentRepository.save(documentMapper.toEntity(docDto));

				QualityControlUpdateDto qc = new QualityControlUpdateDto();
				qc.setIdentifier(faker.number().digits(10));
				qc.setControlDate(generateRandomDateTimeInPast());
				qc.setGranularity((float) faker.number().numberBetween(150, 451));
				qc.setKorTest((float) faker.number().numberBetween(10, 51));
				qc.setHumidity((float) faker.number().numberBetween(0, 101));
				qc.setQualityInspectorId(inspector.getId());
				qc.setQualityId(quality.getId());
				QualityControl qualityControl = qualityControlRepository
						.save(qualityControlMapper.toEntity(qc));

				HarvestProductUpdateDto dto = new HarvestProductUpdateDto();
				dto.setProducerId(producer.getId());
				dto.setStoreId(store.getId());
				dto.setFieldId(field.getId());
				dto.setWeightKg((double) faker.number().numberBetween(MIN_PRODUCT_50KG_BAG,
						MAX_PRODUCT_50KG_BAG + 1) * 50);
				dto.setDeliveryDate(generateRandomDateTimeInPast());
				dto.setQualityControlId(qualityControl.getId());

				try {
					ProductDto created = productService.createProduct(dto);
					createdProducts.add(created);
				} catch (Exception e) {
					log.error("Failed to create harvest product for producer {}: {}",
							producer.getId(), e.getMessage());
				}
			}
		}
		log.info("Harvest products created ({}).", createdProducts.size());
	}

	private void createTransformedProducts() {
		if (createdTransformers.isEmpty()) {
			log.warn("No transformers available to create transformed products.");
			return;
		}
		log.info("Creating Transformed Products…");

		for (TransformerDetailDto transformer : createdTransformers) {
			// Liste des HP gagnés par ce transformeur
			List<Integer> pool = transformerWonHarvestProductsMap.getOrDefault(transformer.getId(),
					Collections.emptyList());

			int numProducts = random
					.nextInt(MAX_PRODUCTS_PER_TRANSFORMER - MIN_PRODUCTS_PER_TRANSFORMER + 1)
					+ MIN_PRODUCTS_PER_TRANSFORMER;

			for (int i = 0; i < numProducts; i++) {
				StoreDetailDto store = createdStores.get(random.nextInt(createdStores.size()));
				QualityDto quality = amandeQualities.get(random.nextInt(amandeQualities.size()));

				DocumentUpdateDto docDto = new DocumentUpdateDto();
				String[] extensions = {"pdf", "docx", "xlsx", "jpg", "png"};
				docDto.setExtension(faker.options().option(extensions));
				docDto.setContentType("application/" + docDto.getExtension());
				docDto.setOriginalFilename(
						faker.file().fileName(null, null, docDto.getExtension(), null));
				docDto.setSize(faker.number().numberBetween(128, 4097));
				docDto.setStoragePath("/documents/2025/transformed_" + faker.number().digits(10)
						+ "." + docDto.getExtension());
				docDto.setUploadDate(generateRandomDateTimeInPast());
				docDto.setUserId(transformer.getId());
				Document document = documentRepository.save(documentMapper.toEntity(docDto));

				QualityInspectorDetailDto inspector = createdQualityInspectors
						.get(random.nextInt(createdQualityInspectors.size()));
				QualityControlUpdateDto qc = new QualityControlUpdateDto();
				qc.setIdentifier(faker.number().digits(10));
				qc.setControlDate(generateRandomDateTimeInPast());
				qc.setGranularity((float) faker.number().numberBetween(150, 451));
				qc.setKorTest((float) faker.number().numberBetween(10, 51));
				qc.setHumidity((float) faker.number().numberBetween(0, 101));
				qc.setQualityInspectorId(inspector.getId());
				qc.setQualityId(quality.getId());
				QualityControl qualityControl = qualityControlRepository
						.save(qualityControlMapper.toEntity(qc));

				TransformedProductUpdateDto dto = new TransformedProductUpdateDto();
				dto.setTransformerId(transformer.getId());
				dto.setStoreId(store.getId());
				dto.setIdentifier("TRANS-" + faker.letterify("??????").toUpperCase());
				dto.setWeightKg((double) faker.number().numberBetween(MIN_PRODUCT_50KG_BAG,
						MAX_PRODUCT_50KG_BAG + 1) * 50);
				dto.setDeliveryDate(generateRandomDateTimeInPast());
				dto.setQualityControlId(qualityControl.getId());

				// harvest product IDs – uniquement ceux gagnés par ce transformeur
				int nbHarvestProducts = faker.number().numberBetween(
						MIN_HARVEST_PRODUCT_PER_TRANSFORMED_PRODUCT,
						MAX_HARVEST_PRODUCT_PER_TRANSFORMED_PRODUCT + 1);
				if (!pool.isEmpty()) {
					List<Integer> shuffled = new ArrayList<>(pool);
					Collections.shuffle(shuffled, random);
					List<Integer> harvestProductIds = new ArrayList<>(
							shuffled.subList(0, Math.min(nbHarvestProducts, shuffled.size())));
					dto.setHarvestProductIds(harvestProductIds);
				}

				try {
					ProductDto created = productService.createProduct(dto);
					createdProducts.add(created);
				} catch (Exception e) {
					log.error("Failed to create transformed product for transformer {}: {}",
							transformer.getId(), e.getMessage());
				}
			}
		}
		log.info("Transformed products created (total products list size: {}).",
				createdProducts.size());
	}

	private void createAuctions(boolean onlyTransformed) {
		if (createdProducts.isEmpty()) {
			log.warn("No products available to create auctions.");
			return;
		}

		/*
		 * 1) Produits concernés
		 */
		List<ProductDto> productsForAuction = createdProducts.stream()
				.filter(p -> onlyTransformed
						? p instanceof TransformedProductDto
						: p instanceof HarvestProductDto)
				.toList();
		if (productsForAuction.isEmpty()) {
			log.warn("No products matching onlyTransformed={}", onlyTransformed);
			return;
		}

		/*
		 * 2) Pool de vendeurs valides
		 */
		List<TraderDetailDto> sellerPool = onlyTransformed
				? new ArrayList<>(createdTransformers)
				: new ArrayList<>(createdProducers);
		if (sellerPool.isEmpty()) {
			log.warn("No sellers available for onlyTransformed={}", onlyTransformed);
			return;
		}

		log.info("Creating auctions – type={}, sellers={}, items={} (1 per product)",
				onlyTransformed ? "TRANSFORMED" : "HARVEST", sellerPool.size(),
				productsForAuction.size());

		/*
		 * 3) Parcours de chaque vendeur et de ses produits
		 */
		for (TraderDetailDto seller : sellerPool) {
			// Sous-ensemble des produits appartenant à ce vendeur
			List<ProductDto> sellerProducts = productsForAuction.stream().filter(p -> {
				if (p instanceof HarvestProductDto hp) {
					return hp.getProducer().getId().equals(seller.getId());
				} else if (p instanceof TransformedProductDto tp) {
					return tp.getTransformer().getId().equals(seller.getId());
				}
				return false;
			}).toList();

			for (ProductDto product : sellerProducts) {
				createSingleAuction(product, seller);
			}
		}

		log.info("Auctions created (Total list size: {}).", createdAuctions.size());
	}

	private void createSingleAuction(ProductDto product, TraderDetailDto seller) {
		LocalDateTime creationDate = generateRandomDateTimeInPast();
		LocalDateTime expirationDate = creationDate.plusDays(
				faker.number().numberBetween(MIN_AUCTION_DURATION_DAYS, MAX_AUCTION_DURATION_DAYS));

		TradeStatusDto status;
		boolean isActive;
		if (expirationDate.isAfter(generationTime)) {
			status = statusOpen;
			isActive = true;
		} else {
			int statusChoice = random.nextInt(10);
			status = statusChoice < 8 ? statusConcluded : statusExpired;
			isActive = false;
		}

		String qualityName = product.getQualityControl().getQuality().getName();
		int quantity = faker.number().numberBetween(MIN_PRODUCT_50KG_BAG, MAX_PRODUCT_50KG_BAG + 1)
				* 50;
		double[] range = cashewPriceRanges.getOrDefault(qualityName, new double[]{500.0, 800.0});
		double minPrice = range[0];
		double maxPrice = range[1];
		double price = minPrice + random.nextDouble() * (maxPrice - minPrice) / 2;
		BigDecimal total = BigDecimal.valueOf(price * quantity);
		BigDecimal roundedTo100 = total.divide(BigDecimal.valueOf(100), 0, RoundingMode.FLOOR)
				.multiply(BigDecimal.valueOf(100));
		price = roundedTo100.doubleValue();

		AuctionOptionsUpdateDto opt = new AuctionOptionsUpdateDto();
		opt.setStrategyId(strategyOffer.getId());
		opt.setBuyNowPrice(maxPrice * quantity);
		opt.setShowPublic(true);
		opt.setForceBetterBids(faker.bool().bool());
		opt.setMinPriceKg(minPrice);
		opt.setMaxPriceKg(maxPrice);
		opt.setMinIncrement(
				faker.number().numberBetween(MIN_AUCTION_INCREMENT, MAX_AUCTION_INCREMENT + 1)
						* 1000);

		AuctionUpdateDto auctionDto = new AuctionUpdateDto();
		auctionDto.setProductId(product.getId());
		auctionDto.setTraderId(seller.getId());
		auctionDto
				.setProductQuantity(Math.min(quantity, product.getWeightKgAvailable().intValue()));
		auctionDto.setPrice(price);
		auctionDto.setActive(isActive);
		auctionDto.setExpirationDate(expirationDate);
		auctionDto.setStatusId(status.getId());
		auctionDto.setOptions(opt);

		try {
			AuctionDto created = auctionService.createAuction(auctionDto);
			created.setCreationDate(creationDate);
			createdAuctions.add(created);
			auctionRepository.overrideCreationDateNative(created.getId(), creationDate);
		} catch (Exception e) {
			log.error("Failed to create auction for trader {}: {}", seller.getId(), e.getMessage());
		}
	}

	private void createBidsForFinishedAuctions() {
		log.info("Generating bids (context‑aware buyers)…");

		int bidsCreated = 0;
		for (AuctionDto auction : createdAuctions) {
			// éviter de dupliquer les offres si on passe plusieurs fois
			if (auctionsWithBids.contains(auction.getId())) continue;

			List<TraderDetailDto> potentialBidders;
			if (auction.getProduct() instanceof HarvestProductDto) {
				// Acheteurs possibles : transformeurs + exportateurs
				potentialBidders = new ArrayList<>();
				potentialBidders.addAll(createdTransformers);
				potentialBidders.addAll(createdExporters);
			} else if (auction.getProduct() instanceof TransformedProductDto) {
				// Acheteurs possibles : uniquement exportateurs
				potentialBidders = new ArrayList<>(createdExporters);
			} else {
				continue; // Produit inattendu
			}
			if (potentialBidders.isEmpty()) continue;

			Integer ownerId = auction.getTrader().getId();
			potentialBidders = potentialBidders.stream().filter(t -> !t.getId().equals(ownerId))
					.toList();
			if (potentialBidders.isEmpty()) continue;

			int numBids = random
					.nextInt(MAX_BIDS_PER_FINISHED_AUCTION - MIN_BIDS_PER_FINISHED_AUCTION + 1)
					+ MIN_BIDS_PER_FINISHED_AUCTION;
			if (numBids == 0) {
				auctionsWithBids.add(auction.getId());
				continue;
			}

			boolean auctionFinished = !auction.getStatus().getId().equals(statusOpen.getId());
			boolean auctionConcluded = auction.getStatus().getId().equals(statusConcluded.getId());

			BigDecimal currentHighest = BigDecimal.valueOf(auction.getPrice());
			LocalDateTime lastBidTime = auction.getCreationDate();
			List<BidUpdateDto> bids = new ArrayList<>();

			for (int i = 0; i < numBids; i++) {
				TraderDetailDto bidder = potentialBidders
						.get(random.nextInt(potentialBidders.size()));

				// borne temporelle max pour cette offre
				LocalDateTime maxBidTime = auction.getExpirationDate().isBefore(generationTime)
						? auction.getExpirationDate()
						: generationTime;

				if (lastBidTime.isAfter(maxBidTime.minusMinutes(1))) {
					lastBidTime = maxBidTime.minusMinutes(1);
				}
				if (lastBidTime.equals(maxBidTime)) break;

				long startMillis = lastBidTime.plusSeconds(1).atZone(ZoneId.systemDefault())
						.toInstant().toEpochMilli();
				long endMillis = maxBidTime.atZone(ZoneId.systemDefault()).toInstant()
						.toEpochMilli();
				if (startMillis >= endMillis) break;

				// tirage biaisé vers le début
				double u = random.nextDouble();
				double pow = 2.0;
				long offset = (long) ((endMillis - startMillis) * Math.pow(u, pow));
				long bidTime = startMillis + offset;
				LocalDateTime bidCreation = Instant.ofEpochMilli(bidTime)
						.atZone(ZoneId.systemDefault()).toLocalDateTime();
				lastBidTime = bidCreation;

				BidUpdateDto bid = new BidUpdateDto();
				bid.setAuctionId(auction.getId());
				bid.setTraderId(bidder.getId());
				bid.setCreationDate(bidCreation);

				BigDecimal raw = currentHighest
						.multiply(BigDecimal.valueOf(1 + random.nextDouble() * 0.05));
				BigDecimal minInc = BigDecimal.valueOf(auction.getOptions().getMinIncrement());
				BigDecimal amount = raw.divide(minInc, 0, RoundingMode.FLOOR).multiply(minInc);
				BigDecimal buyNowTotal = BigDecimal.valueOf(auction.getOptions().getBuyNowPrice());

				if (amount.compareTo(buyNowTotal) >= 0) {
					if (auctionConcluded) {
						bid.setStatusId(statusAccepted.getId());
						bid.setAmount(buyNowTotal);
						bids.add(bid);
					}
					break;
				} else {
					currentHighest = amount;
					bid.setStatusId(auctionFinished ? statusRejected.getId() : statusOpen.getId());
					bid.setAmount(amount);
					bids.add(bid);
				}
			}

			/*
			 * 4) Acceptation de la meilleure offre si vente conclue
			 */
			if (auctionFinished && auctionConcluded && !bids.isEmpty()) {
				BidUpdateDto winner = bids.stream()
						.max(Comparator.comparing(BidUpdateDto::getAmount)).orElseThrow();
				winner.setStatusId(statusAccepted.getId());

				// Si un transformeur a gagné une récolte, on l’ajoute à la map
				if (auction.getProduct() instanceof HarvestProductDto hp && createdTransformers
						.stream().anyMatch(t -> t.getId().equals(winner.getTraderId()))) {
					transformerWonHarvestProductsMap
							.computeIfAbsent(winner.getTraderId(), k -> new ArrayList<>())
							.add(hp.getId());
				}
			}

			/*
			 * 5) Persistance
			 */
			for (BidUpdateDto dto : bids) {
				try {
					BidDto created = bidService.createBid(dto);
					bidRepository.overrideCreationDateNative(created.getId(),
							dto.getCreationDate());
					bidsCreated++;
				} catch (Exception e) {
					log.error("Error creating bid for auction {}: {}", auction.getId(),
							e.getMessage());
				}
			}
			auctionsWithBids.add(auction.getId());
		}
		log.info("Bids generated: {}", bidsCreated);
	}

	private void createArticles() {
		if (newsRepository.count() > 0) {
			log.info("Des articles existent déjà, on ne ré-injecte pas les données de test.");
			return;
		}

		/* Récupération des catégories */
		NewsCategory alertCat = newsCategoryRepository.findByName("Alertes terrain").orElseThrow();
		NewsCategory marketCat = newsCategoryRepository.findByName("Marché & Prix").orElseThrow();
		NewsCategory researchCat = newsCategoryRepository.findByName("Recherche & Innovations")
				.orElseThrow();
		NewsCategory trainingCat = newsCategoryRepository.findByName("Formation & Ressources")
				.orElseThrow();

		/* ------------------------------------------------------------------ */
		/* 1) Alerte terrain – charançon */
		/* ------------------------------------------------------------------ */
		newsRepository.save(News.builder()
				.title("Alerte : foyers de charançon du cajou détectés dans l’Atacora")
				.content(
						"""
								<p>Le Service phytosanitaire du Bénin signale l’apparition de plusieurs foyers
								de charançon du cajou (<em>Analeptes trifasciata</em>) dans les communes
								de Natitingou et Boukoumbé. Les premières larves ont été observées sur des
								vergers de moins de cinq ans.</p>

								<p>Le charançon s’attaque principalement au tronc, provoquant des galeries
								qui fragilisent les arbres et réduisent le rendement de 20 % en moyenne
								après deux ans d’infestation.</p>

								<h3>Mesures recommandées (sous 72 h)</h3>
								<ul>
								  <li>Retirer et brûler tous les rameaux perforés.</li>
								  <li>Appliquer une solution de neem (30 ml/L) sur les troncs
								      ou un pyréthrinoïde homologué.</li>
								  <li>Mettre en place des pièges lumineux en périphérie du verger.</li>
								  <li>Signaler toute nouvelle infestation via l’application e-Anacarde,
								      menu « Signaler un problème ».</li>
								</ul>

								<p>Des équipes d’experts se déplaceront sur site à partir
								du <strong>3 juin 2025</strong> pour accompagner les producteurs affectés.
								Contact : 94 00 00 00 (WhatsApp).</p>
								""")
				.publicationDate(LocalDateTime.now().minusDays(2)).category(alertCat)
				.authorName("Direction de la Protection des Végétaux").build());

		/* ------------------------------------------------------------------ */
		/* 2) Marché & Prix – hausse CIF Vietnam */
		/* ------------------------------------------------------------------ */
		newsRepository.save(News.builder()
				.title("Marché international : le prix CIF Vietnam progresse de 8 % en mai 2025")
				.content(
						"""
								<p>Les prix CIF des noix brutes d’anacarde livrées à Hô-Chi-Minh-Ville ont
								atteint <strong>1 470 USD/t</strong> fin mai 2025, soit une hausse de
								<strong>8 %</strong> par rapport à avril.</p>

								<table style="border-collapse:collapse;text-align:center">
								  <thead>
								    <tr><th>Origine</th><th>Avril 2025</th><th>Mai 2025</th><th>Variation</th></tr>
								  </thead>
								  <tbody>
								    <tr><td>Bénin</td><td>1 350 USD</td><td>1 460 USD</td><td>+8,1 %</td></tr>
								    <tr><td>Côte d’Ivoire</td><td>1 340 USD</td><td>1 440 USD</td><td>+7,5 %</td></tr>
								  </tbody>
								</table>

								<p>Cette hausse s’explique par :</p>
								<ul>
								  <li>un stock asiatique au plus bas depuis trois ans ;</li>
								  <li>des pluies précoces ayant retardé la collecte dans le Golfe de Guinée ;</li>
								  <li>un renforcement de la demande indienne avant le festival de Diwali.</li>
								</ul>

								<p><strong>Perspectives</strong> – les analystes anticipent un plateau autour
								de 1 500 USD/t si la qualité des noix reste stable en juin. À court terme,
								la prime béninoise devrait se maintenir (20 USD/t) grâce aux récents
								investissements dans la logistique portuaire de Cotonou.</p>
								""")
				.publicationDate(LocalDateTime.now().minusDays(5)).category(marketCat)
				.authorName("Cellule Analyse Marché e-Anacarde").build());

		/* ------------------------------------------------------------------ */
		/* 3) Recherche & Innovations */
		/* ------------------------------------------------------------------ */
		newsRepository.save(News.builder()
				.title("Fertilisation organique : +15 % de rendement prouvé sur parcelles pilotes")
				.content("""
						<p>Une étude de l’Université de Parakou (2023-2024) démontre qu’un apport
						annuel de <strong>4 t/ha de compost</strong> à base de coques d’anacarde
						augmente le rendement en noix brutes de 15 % et diminue de moitié les
						besoins en N-P-K.</p>

						<h3>Méthodologie</h3>
						<p>Vingt parcelles pilotes (2 ha chacune) réparties entre Borgou et Donga,
						comparées à des témoins non fertilisés. Mesure continue de la biomasse,
						de la teneur foliaire en azote et du calibre des noix.</p>

						<h3>Résultats détaillés</h3>
						<ul>
						  <li>Gain moyen : +390 kg/ha.</li>
						  <li>Amélioration du taux de germination à 95 % (+7 pts).</li>
						  <li>Diminution de 32 % de l’incidence des anthracnoses.</li>
						</ul>

						<p>Les chercheurs recommandent d’incorporer le compost pendant la courte
						saison sèche (août-septembre) pour optimiser la minéralisation. Un guide
						pratique sera publié dans la rubrique <em>Formation &amp; Ressources</em>
						d’ici juillet 2025.</p>
						""").publicationDate(LocalDateTime.now().minusDays(10))
				.category(researchCat).authorName("Dr A. Ogouchi, Univ. de Parakou").build());

		/* ------------------------------------------------------------------ */
		/* 4) Formation (vidéo) */
		/* ------------------------------------------------------------------ */
		newsRepository.save(News.builder()
				.title("Vidéo : séchage solaire des noix – réduire l’humidité à 10 % en 48 h")
				.content(
						"""
								<p>Apprenez à construire un séchoir solaire tunnel avec des matériaux
								disponibles localement et à ramener l’humidité des noix brutes sous le
								seuil critique de 10 % en deux jours.</p>

								<div data-youtube-video="">
								  <iframe class="aspect-video w-full" src="https://www.youtube-nocookie.com/embed/NsZjHmRaM94?modestbranding=1&amp;rel=1"
								          allowfullscreen></iframe>
								</div>

								<h3>Points clés</h3>
								<ul>
								  <li>Orientation plein sud pour maximiser le rayonnement.</li>
								  <li>Film polyéthylène 200 µm : durée de vie estimée 3 ans.</li>
								  <li>Tamisage toutes les 6 h pour un séchage homogène.</li>
								</ul>
								""")
				.publicationDate(LocalDateTime.now().minusDays(1)).category(trainingCat)
				.authorName("Centre de Formation Agricole de Savalou").build());

		/* ------------------------------------------------------------------ */
		/* 5) Formation (guide écrit) */
		/* ------------------------------------------------------------------ */
		newsRepository.save(News.builder()
				.title("Guide pratique : taille des jeunes anacardiers – campagne 2025")
				.content(
						"""
								<p>La taille de formation entre la 2<sup>e</sup> et la 4<sup>e</sup> année est
								essentielle pour obtenir une ramification équilibrée et faciliter la
								récolte.</p>

								<h3>Étapes recommandées</h3>
								<ol>
								  <li>Supprimer les gourmands et branches basses &lt; 60 cm du sol.</li>
								  <li>Conserver 3 à 4 charpentières orientées à 120°.</li>
								  <li>Appliquer une pâte cicatrisante (bouillie bordelaise) sur les coupes.</li>
								</ol>

								<p>Une bonne taille améliore la circulation de l’air, réduit la pression des
								maladies fongiques et facilite la pulvérisation.</p>

								<blockquote>
								  <p><strong>Résultat attendu</strong> : +12 % de rendement moyen sur les trois
								  premières récoltes, baisse des anthracnoses de 30 %.</p>
								</blockquote>
								""")
				.publicationDate(LocalDateTime.now().minusDays(3)).category(trainingCat)
				.authorName("Réseau des Producteurs-Experts du Borgou").build());

		/* ------------------------------------------------------------------ */
		/* 6) Alerte : pluies abondantes et risque fongique */
		/* ------------------------------------------------------------------ */
		newsRepository.save(News.builder()
				.title("Alerte météo : pluies intenses prévues – risque de pourriture brune")
				.content("""
						<p>La Direction générale de la Météorologie annonce des précipitations
						supérieures à 70 mm/24 h entre le 4 et le 6 juin 2025 dans les départements
						du Mono et du Couffo.</p>

						<p>Ces conditions favorisent la <em>pourriture brune</em>
						(<em>Phytophthora palmivora</em>). Les producteurs sont invités à :</p>
						<ul>
						  <li>assurer un drainage efficace autour des troncs ;</li>
						  <li>pulvériser préventivement un fongicide à base de cuivre
						      (dose homologuée) dès la première pluie intense ;</li>
						  <li>éviter tout piétinement inutile qui tasse le sol.</li>
						</ul>

						<p>Toute apparition de lésions noirâtres sur les jeunes fruits doit être
						photographiée et signalée via l’application.</p>
						""").publicationDate(LocalDateTime.now().minusDays(1)).category(alertCat)
				.authorName("Service Météo-Agro e-Anacarde").build());

		/* ------------------------------------------------------------------ */
		/* 7) Formation : densité de plantation */
		/* ------------------------------------------------------------------ */
		newsRepository.save(News.builder()
				.title("Optimiser la densité de plantation : 156 arbres/ha ou 204 arbres/ha ?")
				.content("""
						<p>Choisir la bonne densité influence directement le rendement et
						la facilité de la récolte. Deux spacings dominent dans les vergers
						béninois :</p>

						<ul>
						  <li><strong>8 m × 8 m</strong> (156 arbres/ha) : meilleure circulation
						      de la lumière, idéal pour sols peu fertiles.</li>
						  <li><strong>7 m × 7 m</strong> (204 arbres/ha) : rendement plus
						      élevé les huit premières années mais nécessite un éclaircissage
						      à partir de l’année 12.</li>
						</ul>

						<h3>Recommandation 2025</h3>
						<p>Pour les nouveaux vergers sur sols ferrugineux du Borgou,
						un compromis à 7,5 m × 7,5 m est conseillé, associé à une taille
						de formation stricte.</p>

						<p>Un calculateur de densité est disponible dans le menu
						<em>Outils&nbsp;&gt;&nbsp;Calculs techniques</em> de l’application.</p>
						""").publicationDate(LocalDateTime.now().minusDays(4)).category(trainingCat)
				.authorName("Projet PADFA Bénin").build());

		log.info("Articles de démonstration initialisés (7).");
	}

	/** Génère une adresse email unique pour tout le run */
	private String uniqueEmail() {
		String email;
		do {
			email = faker.internet().emailAddress();
		} while (!generatedEmails.add(email)); // add() renvoie false si déjà présent
		return email;
	}

	/** Génère un numéro de téléphone unique pour tout le run */
	private String uniquePhone() {
		String phone;
		do {
			phone = "+22901" + faker.phoneNumber().subscriberNumber(8);
		} while (!generatedPhones.add(phone)); // add() renvoie false si déjà présent
		return phone;
	}

	private LocalDateTime generateRandomDateTimeInPast() {
		Instant startOfYear = ZonedDateTime
				.of(DatabaseServiceImpl.TARGET_YEAR, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
				.toInstant();
		// Generate a date between start of year and now (or end of year if generationTime is after
		// target year)
		Instant end = generationTime.atZone(ZoneId.systemDefault()).toInstant();
		if (end.isAfter(ZonedDateTime
				.of(DatabaseServiceImpl.TARGET_YEAR, 12, 31, 23, 59, 59, 0, ZoneId.systemDefault())
				.toInstant())) {
			end = ZonedDateTime.of(DatabaseServiceImpl.TARGET_YEAR, 12, 31, 23, 59, 59, 0,
					ZoneId.systemDefault()).toInstant();
		}
		if (startOfYear.isAfter(end)) { // Ensure start is not after end
			return LocalDateTime.ofInstant(startOfYear, ZoneId.systemDefault());
		}

		Date randomDate = faker.date().between(Date.from(startOfYear), Date.from(end));
		return LocalDateTime.ofInstant(randomDate.toInstant(), ZoneId.systemDefault());
	}

	private TradeStatusDto createTradeStatusDto(String name) {
		TradeStatusDto status = new TradeStatusDto();
		status.setName(name);
		return status;
	}

	/**
	 * Crée une adresse aléatoire pondérée par la production d'anacarde par région.
	 */
	public AddressDto createRandomAddress() {
		Region selectedRegion = selectRegionByWeight();

		List<City> citiesInRegion = cities.stream()
				.filter(c -> c.getRegion().equals(selectedRegion)).collect(Collectors.toList());
		if (citiesInRegion.isEmpty()) {
			citiesInRegion = new ArrayList<>(cities);
		}

		City selectedCity = citiesInRegion.get(random.nextInt(citiesInRegion.size()));

		Point orig = selectedCity.getLocation();
		double lon0 = orig.getX();
		double lat0 = orig.getY();
		double lon = lon0 + (random.nextDouble() * 0.2 - 0.1);
		double lat = lat0 + (random.nextDouble() * 0.2 - 0.1);
		Point newPoint = geometryFactory.createPoint(new Coordinate(lon, lat));
		String newWkt = newPoint.toText();

		return AddressDto.builder().street(faker.address().streetAddress()).location(newWkt)
				.regionId(selectedRegion.getId()).cityId(selectedCity.getId()).build();
	}

	/**
	 * Construit une AddressDto pour un champ en reprenant la même région et ville que le
	 * producteur, mais avec rue aléatoire et décalage ±0.1° sur le point.
	 */
	private AddressDto createVariationAddress(AddressDto addressDto) {
		Integer regionId = addressDto.getRegionId();
		Integer cityId = addressDto.getCityId();

		City baseCity = cities.stream().filter(c -> c.getId().equals(cityId)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Ville du producteur introuvable"));

		Point orig = baseCity.getLocation();
		double lon0 = orig.getX();
		double lat0 = orig.getY();
		double lon = lon0 + (random.nextDouble() * 0.2 - 0.1);
		double lat = lat0 + (random.nextDouble() * 0.2 - 0.1);
		Point newPoint = geometryFactory.createPoint(new Coordinate(lon, lat));

		return AddressDto.builder().street(faker.address().streetAddress()) // rue aléatoire
				.location(newPoint.toText()) // WKT du nouveau point
				.regionId(regionId) // même région que le producteur
				.cityId(cityId) // même ville que le producteur
				.build();
	}

	/**
	 * Sélectionne une région en fonction des poids de production.
	 */
	private Region selectRegionByWeight() {
		double r = random.nextDouble();
		double cumulative = 0.0;
		for (int i = 0; i < regions.size(); i++) {
			cumulative += regionWeights[i];
			if (r <= cumulative) {
				return regions.get(i);
			}
		}
		// En cas de somme < 1, retourner la dernière région
		return regions.getLast();
	}

	private String capitalize(String s) {
		return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
	}

	private UserCreateDto setUserDetails(UserCreateDto user) {
		String mail = uniqueEmail();
		String localPart = mail.split("@", 2)[0];
		String[] nameParts = localPart.split("\\.", 2);
		user.setEmail(mail);
		user.setFirstName(nameParts[0]);
		user.setLastName(nameParts[1]);
		user.setEmail(uniqueEmail());
		user.setPassword(DEFAULT_PASSWORD);
		user.setAddress(createRandomAddress());
		user.setEnabled(true);
		user.setRegistrationDate(generateRandomDateTimeInPast());
		user.setValidationDate(
				user.getRegistrationDate().plusDays(faker.number().numberBetween(1, 4)));
		user.setPhone(this.uniquePhone());
		user.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return user;
	}

	private AdminCreateDto createRandomAdminDto() {
		AdminCreateDto admin = new AdminCreateDto();
		return (AdminCreateDto) setUserDetails(admin);
	}

	private ProducerCreateDto createRandomProducerDto() {
		ProducerCreateDto producer = new ProducerCreateDto();
		producer.setAgriculturalIdentifier(faker.number().digits(10));
		return (ProducerCreateDto) setUserDetails(producer);
	}

	private ProducerCreateDto createRandomPendingProducerDto() {
		ProducerCreateDto pendingProducer = createRandomProducerDto();
		pendingProducer.setValidationDate(null);
		pendingProducer.setEnabled(false);
		return pendingProducer;
	}

	private TransformerCreateDto createRandomTransformerDto() {
		TransformerCreateDto transformer = new TransformerCreateDto();
		return (TransformerCreateDto) setUserDetails(transformer);
	}

	private ExporterCreateDto createRandomExporterDto() {
		ExporterCreateDto exporter = new ExporterCreateDto();
		return (ExporterCreateDto) setUserDetails(exporter);
	}

	private CarrierCreateDto createRandomCarrierDto() {
		CarrierCreateDto carrier = new CarrierCreateDto();
		carrier.setPricePerKm(faker.number().randomDouble(2, 50, 500) / 100.0);
		carrier.setRadius(faker.number().randomDouble(0, 10, 100));
		return (CarrierCreateDto) setUserDetails(carrier);
	}

	private QualityInspectorCreateDto createRandomQualityInspectorDto() {
		QualityInspectorCreateDto qualityInspector = new QualityInspectorCreateDto();
		return (QualityInspectorCreateDto) setUserDetails(qualityInspector);
	}
}