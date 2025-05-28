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
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
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
	private static final String CREATE_VIEW_SQL = """
			/* ===============================================================
			   v_auction_bid_analysis  :  1 ligne par enchère + agrégats bids
			   ===============================================================*/
			CREATE OR REPLACE VIEW v_auction_bid_analysis AS
			/* ---------- 1) UNION des produits concrets ---------- */
			WITH all_products AS (
			    SELECT hp.id,
			           hp.weight_kg,
			           hp.delivery_date,
			           hp.quality_control_id,
			           hp.store_id,
					   hp.transformed_product_id
			    FROM   harvest_product hp

			    UNION ALL

			    SELECT tp.id,
			           tp.weight_kg,
			           tp.delivery_date,
			           tp.quality_control_id,
			           tp.store_id,
					   NULL::bigint AS transformed_product_id
			    FROM   transformed_product tp
			),
			/* ---------- 2) Offre gagnante = bid dont statut 'Conclu' ---------- */
			winner AS (
			    SELECT DISTINCT ON (b.auction_id)
			           b.auction_id,
			           b.amount      AS winning_bid_amount,
			           b.trader_id   AS winner_trader_id,
			           b.status_id
			    FROM   bid b
			    WHERE  b.status_id = 2
			    ORDER  BY b.auction_id, b.amount DESC, b.creation_date DESC
			)
			/* ---------- 3) Résultat final ---------- */
			SELECT
			    /* ======== auction ======== */
			    a.id                             AS auction_id,
			    a.creation_date                  AS auction_start_date,
			    a.expiration_date                AS auction_end_date,
			    a.price                          AS auction_start_price,
			    a.active                         AS auction_ended,
			    ts.name                          AS auction_status,

			    /* options embarquées */
			    ast.name                         AS strategy_name,
			    a.min_price_kg                   AS option_min_price_kg,
			    a.max_price_kg                   AS option_max_price_kg,
			    a.buy_now_price                  AS option_buy_now_price,
			    a.show_public                    AS option_show_public,
			    a.min_increment                  AS option_min_increment,

			    /* ======== produit ======== */
			    p.id                             AS product_id,
			    p.weight_kg                      AS product_weight_kg,
			    p.delivery_date                  AS product_deposit_date,
			    p.transformed_product_id         AS transformed_product_id,

			    /* -------- quality control -------- */
			    qc.quality_inspector_id          AS quality_inspector_id,
			    q.name                           AS product_quality,
			    qt.name                          AS product_type,

			    /* ======== store du produit ======== */
			    st.id                            AS store_id,
			    st.name                          AS store_name,
			    stc.name                         AS store_city,
			    str.name                         AS store_region,

			    /* ======== vendeur ======== */
			    tr.id                            AS seller_id,
			    sc.name                          AS seller_city,
			    sr.name                          AS seller_region,
			    co.name                          AS seller_cooperative,

			    /* ======== agrégats bids ======== */
			    COUNT(b.id)                      AS bid_count,
			    MAX(b.amount)                    AS bid_max,
			    MIN(b.amount)                    AS bid_min,
			    ROUND(AVG(b.amount), 2)          AS bid_avg,
			    SUM(b.amount)                    AS bid_sum,

			    /* ======== offre gagnante ======== */
			    w.winner_trader_id,
			    w.winning_bid_amount             AS bid_winning_amount,
			    wc.name                          AS winner_city,
			    wr.name                          AS winner_region

			FROM   auction a
			LEFT   JOIN auction_strategy ast ON ast.id = a.strategy_id
			JOIN   trade_status ts          ON ts.id = a.status_id

			/* ---------- produit & quality control ---------- */
			JOIN   all_products p           ON p.id = a.product_id
			LEFT   JOIN quality_control qc  ON qc.id = p.quality_control_id
			LEFT   JOIN quality         q   ON q.id  = qc.quality_id
			LEFT   JOIN quality_type    qt  ON qt.id = q.quality_type_id

			/* ---------- store du produit ---------- */
			JOIN   store   st  ON st.id = p.store_id
			JOIN   users   su  ON su.id = st.user_id
			LEFT   JOIN city   stc ON stc.id = st.city_id
			LEFT   JOIN region str ON str.id = st.region_id

			/* ---------- vendeur ---------- */
			JOIN   trader tr ON tr.id = a.trader_id
			JOIN   users  u  ON u.id = tr.id
			LEFT   JOIN producer    pr ON pr.id = tr.id
			LEFT   JOIN cooperative co ON co.id = pr.cooperative_id
			LEFT   JOIN city   sc ON sc.id = u.city_id
			LEFT   JOIN region sr ON sr.id = u.region_id

			/* ---------- bids & gagnant ---------- */
			LEFT   JOIN bid    b  ON b.auction_id = a.id
			LEFT   JOIN winner w  ON w.auction_id = a.id
			LEFT   JOIN trader wt ON wt.id = w.winner_trader_id
			LEFT   JOIN users  wu ON wu.id = wt.id
			LEFT   JOIN city   wc ON wc.id = wu.city_id
			LEFT   JOIN region wr ON wr.id = wu.region_id

			GROUP  BY a.id, ts.name,
			          a.strategy_id, ast.name,
			          a.min_price_kg, a.max_price_kg, a.buy_now_price, a.show_public, a.min_increment,
			          p.id, p.weight_kg, p.delivery_date, p.transformed_product_id,
			          qc.quality_inspector_id, q.name, qt.name,
			          st.id, st.name, su.first_name, su.last_name, stc.name, str.name,
			          tr.id, u.first_name, u.last_name, sc.name, sr.name, co.name,
			          w.winning_bid_amount, w.winner_trader_id,
			          wu.first_name, wu.last_name, wc.name, wr.name;
			""";

	@Override
	public boolean isInitialized() {
		return userRepository.count() > 0;
	}

	@Override
	public void dropDatabase() {
		// Remove all associations with cooperative
		userRepository.findAll().forEach(p -> {
			if (p instanceof Producer c) {
				c.setCooperative(null);
				userRepository.save(c);
			}
		});
		userRepository.flush();
		globalSettingsRepository.deleteAllInBatch();
		// Delete all entities in the correct order to avoid foreign key constraint violations
		cooperativeRepository.deleteAllInBatch();
		contractOfferRepository.deleteAllInBatch();
		bidRepository.deleteAllInBatch();
		auctionRepository.deleteAllInBatch();
		tradeStatusRepository.deleteAllInBatch();
		auctionStrategyRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		qualityControlRepository.deleteAllInBatch();
		qualityRepository.deleteAllInBatch();
		qualityTypeRepository.deleteAllInBatch();
		documentRepository.deleteAllInBatch();
		fieldRepository.deleteAllInBatch();
		storeRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		cityRepository.deleteAllInBatch();
		regionRepository.deleteAllInBatch();
		languageRepository.deleteAllInBatch();
	}

	@Override
	@Transactional
	public void createDatabase() throws IOException {
		log.info("→ createDatabase() (compatibilité) ←");
		initDatabase();
		initTestData();
		em.createNativeQuery(CREATE_VIEW_SQL).executeUpdate();
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

		entityManager.flush();
		log.info("Données de test générées ✔");
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
		globalSettingsUpdateDto.setMinIncrement(1);
		globalSettingsUpdateDto.setShowOnlyActive(false);
		globalSettingsService.updateGlobalSettings(globalSettingsUpdateDto);
		log.debug("Base lookups created.");

		// TODO: AJOUTE EN DB UNE STRATEGIE GLOBALE

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
				qc.setDocumentId(document.getId());
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
				qc.setDocumentId(document.getId());
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
		auctionDto.setProductQuantity(quantity);
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

	private AdminCreateDto createRandomAdminDto() {
		AdminCreateDto admin = new AdminCreateDto();
		admin.setFirstName(faker.name().firstName());
		admin.setLastName(faker.name().lastName());
		admin.setEmail(uniqueEmail());
		admin.setPassword(DEFAULT_PASSWORD);
		admin.setAddress(createRandomAddress());
		admin.setEnabled(true);
		admin.setRegistrationDate(generateRandomDateTimeInPast());
		admin.setValidationDate(
				admin.getRegistrationDate().plusDays(faker.number().numberBetween(1, 4))); // Validated
		admin.setPhone(this.uniquePhone());
		admin.setLanguageId(langFr.getId()); // Default to French for now
		return admin;
	}

	private ProducerCreateDto createRandomProducerDto() {
		ProducerCreateDto producer = new ProducerCreateDto();
		producer.setFirstName(faker.name().firstName());
		producer.setLastName(faker.name().lastName());
		producer.setEmail(uniqueEmail());
		producer.setPassword(DEFAULT_PASSWORD);
		producer.setEnabled(true);
		producer.setAddress(createRandomAddress());
		producer.setRegistrationDate(generateRandomDateTimeInPast());
		producer.setValidationDate(
				producer.getRegistrationDate().plusDays(faker.number().numberBetween(1, 4)));
		producer.setPhone(this.uniquePhone());
		producer.setAgriculturalIdentifier(faker.number().digits(10));
		producer.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return producer;
	}

	private ProducerCreateDto createRandomPendingProducerDto() {
		ProducerCreateDto pendingProducer = createRandomProducerDto();
		pendingProducer.setValidationDate(null);
		pendingProducer.setEnabled(false);
		return pendingProducer;
	}

	private TransformerCreateDto createRandomTransformerDto() {
		TransformerCreateDto transformer = new TransformerCreateDto();
		transformer.setFirstName(faker.name().firstName());
		transformer.setLastName(faker.name().lastName());
		transformer.setEmail(uniqueEmail());
		transformer.setPassword(DEFAULT_PASSWORD);
		transformer.setEnabled(true);
		transformer.setAddress(createRandomAddress());
		transformer.setRegistrationDate(generateRandomDateTimeInPast());
		transformer.setValidationDate(
				transformer.getRegistrationDate().plusDays(faker.number().numberBetween(1, 4)));
		transformer.setPhone(this.uniquePhone());
		transformer.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return transformer;
	}

	private ExporterCreateDto createRandomExporterDto() {
		ExporterCreateDto exporter = new ExporterCreateDto();
		exporter.setFirstName(faker.name().firstName());
		exporter.setLastName(faker.name().lastName());
		exporter.setEmail(uniqueEmail());
		exporter.setPassword(DEFAULT_PASSWORD);
		exporter.setEnabled(true);
		exporter.setAddress(createRandomAddress());
		exporter.setRegistrationDate(generateRandomDateTimeInPast());
		exporter.setValidationDate(
				exporter.getRegistrationDate().plusDays(faker.number().numberBetween(1, 4)));
		exporter.setPhone(this.uniquePhone());
		exporter.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return exporter;
	}

	private CarrierCreateDto createRandomCarrierDto() {
		CarrierCreateDto carrier = new CarrierCreateDto();
		carrier.setFirstName(faker.name().firstName());
		carrier.setLastName(faker.name().lastName());
		carrier.setEmail(uniqueEmail());
		carrier.setPassword(DEFAULT_PASSWORD);
		carrier.setEnabled(Math.random() >= 0.2);
		carrier.setAddress(createRandomAddress());
		carrier.setPricePerKm(faker.number().randomDouble(2, 50, 500) / 100.0);
		carrier.setRadius(faker.number().randomDouble(0, 10, 100));
		carrier.setRegistrationDate(generateRandomDateTimeInPast());
		carrier.setValidationDate(
				carrier.getRegistrationDate().plusDays(faker.number().numberBetween(1, 4)));
		carrier.setPhone(this.uniquePhone());
		carrier.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return carrier;
	}

	private QualityInspectorCreateDto createRandomQualityInspectorDto() {
		QualityInspectorCreateDto qualityInspector = new QualityInspectorCreateDto();
		qualityInspector.setFirstName(faker.name().firstName());
		qualityInspector.setLastName(faker.name().lastName());
		qualityInspector.setEmail(uniqueEmail());
		qualityInspector.setPassword(DEFAULT_PASSWORD);
		qualityInspector.setEnabled(Math.random() >= 0.2);
		qualityInspector.setAddress(createRandomAddress());
		qualityInspector.setRegistrationDate(generateRandomDateTimeInPast());
		qualityInspector.setValidationDate(qualityInspector.getRegistrationDate()
				.plusDays(faker.number().numberBetween(1, 4)));
		qualityInspector.setPhone(this.uniquePhone());
		qualityInspector.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return qualityInspector;
	}
}