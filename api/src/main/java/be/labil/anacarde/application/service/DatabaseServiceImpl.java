package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.*;
import be.labil.anacarde.domain.dto.db.product.HarvestProductDto;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.db.product.TransformedProductDto;
import be.labil.anacarde.domain.dto.db.user.*;
import be.labil.anacarde.domain.dto.write.AuctionOptionsUpdateDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.dto.write.CooperativeUpdateDto;
import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.domain.dto.write.user.*;
import be.labil.anacarde.domain.model.*;
import be.labil.anacarde.infrastructure.import_data.RegionCityImportService;
import be.labil.anacarde.infrastructure.persistence.*;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
	private static final int TARGET_YEAR = 2025; // Année cible pour les données
	private static final int NUM_PRODUCERS = 100;
	private static final int NUM_TRANSFORMERS = 20;
	private static final int NUM_EXPORTERS = 10;
	private static final int NUM_CARRIERS = 10;
	private static final int NUM_QUALITY_INSPECTORS = 10;
	private static final int NUM_ADMINS = 3;
	private static final int NUM_COOPERATIVES = 15;
	private static final int NUM_STORES = 25;
	private static final int MIN_FIELDS_PER_PRODUCER = 1;
	private static final int MAX_FIELDS_PER_PRODUCER = 5;
	private static final int MIN_PRODUCTS_PER_PRODUCER = 3;
	private static final int MAX_PRODUCTS_PER_PRODUCER = 10;
	private static final int MIN_PRODUCTS_PER_TRANSFORMER = 5;
	private static final int MAX_PRODUCTS_PER_TRANSFORMER = 15;
	private static final int NUM_AUCTIONS = 1000;
	private static final int MIN_BIDS_PER_FINISHED_AUCTION = 0;
	private static final int MAX_BIDS_PER_FINISHED_AUCTION = 30;
	private static final String DEFAULT_PASSWORD = "password";

	// --- Injected Dependencies (Final with Lombok RequiredArgsConstructor) ---
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
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

	private final EntityManager entityManager;
	private final ObjectMapper mapper;

	// --- Internal State ---
	private final Faker faker = new Faker(new Locale("fr")); // Use French Faker locale
	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326); // SRID
																										// for
																										// WGS84
	private final Random random = new Random();
	private final QualityTypeRepository qualityTypeRepository;
	private LocalDateTime generationTime; // Timestamp when generation starts
	// juste après 'private final Random random = new Random();'
	private final Set<String> generatedEmails = new HashSet<>();
	private final List<AuctionDto> createdAuctions = new ArrayList<>(); // Reset list for this run

	// Base data loaded once
	private LanguageDto langFr;
	private LanguageDto langEn;
	private TradeStatusDto statusOpen;
	private TradeStatusDto statusAccepted;
	private TradeStatusDto statusExpired;
	private TradeStatusDto statusConcluded; // Assuming 'Conclu' might be another finished status
	private TradeStatusDto statusRejected;
	private TradeStatusDto statusCancelled;
	private AuctionStrategyDto strategyOffer;

	// Lists to hold created DTOs for relationships
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
		roleRepository.deleteAllInBatch();
		cityRepository.deleteAllInBatch();
		regionRepository.deleteAllInBatch();
		languageRepository.deleteAllInBatch();
	}

	@Override
	@Transactional
	public void createDatabase() throws IOException {
		this.generationTime = LocalDateTime.now(); // Set generation timestamp
		log.info("Starting database creation with {} target year at {}", TARGET_YEAR,
				generationTime);

		createBaseLookups();
		createRegionsAndCities();
		createUsers();
		createCooperativesAndAssignMembers();
		createStores();
		createFields();
		createProducts();
		createAuctions();
		createBidsForFinishedAuctions();

		entityManager.flush();
		log.info("Database creation complete for {} target year.", TARGET_YEAR);
	}

	// --- Step 1: Base Lookups ---
	private void createBaseLookups() {
		log.debug("Creating base lookups...");
		// Languages
		langFr = languageService
				.createLanguage(LanguageDto.builder().name("Français").code("fr").build());
		langEn = languageService
				.createLanguage(LanguageDto.builder().name("English").code("en").build());

		// Roles (Ensure they exist - UserService might create them too)
		roleRepository.findByName("ROLE_CARRIER")
				.orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_CARRIER").build()));
		roleRepository.findByName("ROLE_ADMIN")
				.orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));
		roleRepository.findByName("ROLE_PRODUCER")
				.orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_PRODUCER").build()));
		roleRepository.findByName("ROLE_TRANSFORMER").orElseGet(
				() -> roleRepository.save(Role.builder().name("ROLE_TRANSFORMER").build()));
		roleRepository.findByName("ROLE_EXPORTER")
				.orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_EXPORTER").build()));
		roleRepository.findByName("ROLE_QUALITY_CONTROL").orElseGet(
				() -> roleRepository.save(Role.builder().name("ROLE_QUALITY_CONTROL").build()));

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
		log.debug("Base lookups created.");
	}

	// --- Step 2: Regions and Cities ---
	private void createRegionsAndCities() throws IOException {

		/* ---------- 1) Régions ---------- */
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

	// --- Step 3: Users ---
	private void createUsers() {
		log.info(
				"Creating {} Producers, {} Transformers, {} Exporters, {} Admins, {} Carriers, {} Quality_Inspectors",
				NUM_PRODUCERS, NUM_TRANSFORMERS, NUM_EXPORTERS, NUM_ADMINS, NUM_CARRIERS,
				NUM_QUALITY_INSPECTORS);

		createdProducers = IntStream.range(0, NUM_PRODUCERS)
				.mapToObj(i -> userService.createUser(createRandomProducerDto()))
				.map(ProducerDetailDto.class::cast) // Cast UserDetailDto to ProducerDetailDto
				.collect(Collectors.toList());

		createdTransformers = IntStream.range(0, NUM_TRANSFORMERS)
				.mapToObj(i -> userService.createUser(createRandomTransformerDto()))
				.map(TransformerDetailDto.class::cast).collect(Collectors.toList());

		createdExporters = IntStream.range(0, NUM_EXPORTERS)
				.mapToObj(i -> userService.createUser(createRandomExporterDto()))
				.map(ExporterDetailDto.class::cast).collect(Collectors.toList());

		createdAdmins = IntStream.range(0, NUM_ADMINS)
				.mapToObj(i -> userService.createUser(createRandomAdminDto()))
				.map(AdminDetailDto.class::cast).collect(Collectors.toList());

		createdCarriers = IntStream.range(0, NUM_CARRIERS)
				.mapToObj(i -> userService.createUser(createRandomCarrierDto()))
				.map(CarrierDetailDto.class::cast).collect(Collectors.toList());

		createdQualityInspectors = IntStream.range(0, NUM_QUALITY_INSPECTORS)
				.mapToObj(i -> userService.createUser(createRandomQualityInspectorDto()))
				.map(QualityInspectorDetailDto.class::cast).collect(Collectors.toList());

		log.info("Users created.");
	}

	// --- Step 4: Cooperatives ---
	private void createCooperativesAndAssignMembers() {
		if (createdProducers.isEmpty()) {
			log.warn("No producers available to create cooperatives.");
			return;
		}

		log.info("Creating {} Cooperatives...", NUM_COOPERATIVES);
		createdCooperatives = new ArrayList<>();

		// listes de travail
		List<ProducerDetailDto> presidentsPool = new ArrayList<>(createdProducers);
		List<ProducerDetailDto> membersPool = new ArrayList<>(createdProducers); // producteurs
																					// restants

		for (int i = 0; i < NUM_COOPERATIVES && !presidentsPool.isEmpty(); i++) {

			/* ---------- 1) Président ---------- */
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
				ProducerDetailDto memberDto = membersPool.remove(0); // retire du pool pour éviter
																		// doublons
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

	// --- Step 5: Stores ---
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
			Region randomRegion = regions.get(faker.number().numberBetween(0, regions.size()));
			List<City> citiesInRegion = cities.stream()
					.filter(c -> c.getRegion().equals(randomRegion)).collect(Collectors.toList());
			if (citiesInRegion.isEmpty()) {
				citiesInRegion = List.copyOf(cities);
			}
			City randomCity = citiesInRegion
					.get(faker.number().numberBetween(0, citiesInRegion.size()));
			AddressDto addr = AddressDto.builder().street(faker.address().streetAddress())
					.location(randomWktPoint()).regionId(randomRegion.getId())
					.cityId(randomCity.getId()).build();
			storeDto.setAddress(addr);
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

	// --- Step 6: Fields ---
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
				fieldDto.setIdentifier("FIELD-" + faker.number().digits(6));
				Region randomRegion = regions.get(faker.number().numberBetween(0, regions.size()));
				List<City> citiesInRegion = cities.stream()
						.filter(c -> c.getRegion().equals(randomRegion))
						.collect(Collectors.toList());
				if (citiesInRegion.isEmpty()) {
					citiesInRegion = List.copyOf(cities);
				}
				City randomCity = citiesInRegion
						.get(faker.number().numberBetween(0, citiesInRegion.size()));
				AddressDto addr = AddressDto.builder().street(faker.address().streetAddress())
						.location(randomWktPoint()).regionId(randomRegion.getId())
						.cityId(randomCity.getId()).build();
				fieldDto.setAddress(addr);
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

	// --- Step 7 : Products ---
	private void createProducts() {
		if (createdProducers.isEmpty() && createdTransformers.isEmpty()) {
			log.warn("No producers or transformers to create products.");
			return;
		}
		log.info("Creating Products...");
		createdProducts = new ArrayList<>();

		/* -------- Récoltes -------- */
		for (ProducerDetailDto producer : createdProducers) {
			List<FieldDto> fields = producerFieldsMap.getOrDefault(producer.getId(),
					Collections.emptyList());
			if (fields.isEmpty() || createdStores.isEmpty()) continue;

			int numProducts = random
					.nextInt(MAX_PRODUCTS_PER_PRODUCER - MIN_PRODUCTS_PER_PRODUCER + 1)
					+ MIN_PRODUCTS_PER_PRODUCER;

			for (int i = 0; i < numProducts; i++) {
				StoreDetailDto store = createdStores.get(random.nextInt(createdStores.size()));
				FieldDto field = fields.get(random.nextInt(fields.size()));

				/* --- DTO d’écriture --- */
				HarvestProductUpdateDto dto = new HarvestProductUpdateDto();
				dto.setProducerId(producer.getId());
				dto.setStoreId(store.getId());
				dto.setFieldId(field.getId());
				dto.setWeightKg(faker.number().randomDouble(2, 500, 8000)); // 500 kg – 8000 kg
				dto.setDeliveryDate(generateRandomDateTimeInPast());

				try {
					ProductDto created = productService.createProduct(dto); // compile désormais
					createdProducts.add(created);
				} catch (Exception e) {
					log.error("Failed to create harvest product for producer {}: {}",
							producer.getId(), e.getMessage());
				}
			}
		}

		// Transformed Products
		for (TransformerDetailDto transformer : createdTransformers) {
			if (createdStores.isEmpty()) continue;
			int numProducts = random
					.nextInt(MAX_PRODUCTS_PER_TRANSFORMER - MIN_PRODUCTS_PER_TRANSFORMER + 1)
					+ MIN_PRODUCTS_PER_TRANSFORMER;
			for (int i = 0; i < numProducts; i++) {
				StoreDetailDto store = createdStores.get(random.nextInt(createdStores.size()));
				TransformedProductUpdateDto dto = new TransformedProductUpdateDto();
				dto.setTransformerId(transformer.getId());
				dto.setStoreId(store.getId());
				dto.setIdentifier("TRANS-" + faker.letterify("??????").toUpperCase());
				dto.setWeightKg(faker.number().randomDouble(2, 200, 5000)); // 200kg - 5000kg
				dto.setDeliveryDate(generateRandomDateTimeInPast());
				try {
					createdProducts.add(productService.createProduct(dto));
				} catch (Exception e) {
					log.error("Failed to create transformed product for transformer {}: {}",
							transformer.getId(), e.getMessage());
				}
			}
		}
		log.info("Products created (Total: {}).", createdProducts.size());
	}

	// --- Step 8: Auctions ---
	private void createAuctions() {
		if (createdProducts.isEmpty()
				|| (createdProducers.isEmpty() && createdTransformers.isEmpty())) {
			log.warn("Not enough data (products/traders) to create auctions.");
			return;
		}
		log.info("Creating {} Auctions spread across {}...", NUM_AUCTIONS, TARGET_YEAR);
		// Store created auctions
		List<TraderDetailDto> allTraders = new ArrayList<>();
		allTraders.addAll(createdProducers);
		allTraders.addAll(createdTransformers);
		// Add Exporters if they can trade: allTraders.addAll(createdExporters);

		Instant startOfYear = ZonedDateTime
				.of(TARGET_YEAR, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant();
		Instant endOfYear = ZonedDateTime
				.of(TARGET_YEAR, 12, 31, 23, 59, 59, 999_999_999, ZoneId.systemDefault())
				.toInstant();

		for (int i = 0; i < NUM_AUCTIONS; i++) {
			ProductDto product = createdProducts.get(random.nextInt(createdProducts.size()));
			TraderDetailDto trader;

			// Determine trader based on product type
			if (product instanceof HarvestProductDto) {
				// Find the producer DTO associated with this product
				Integer producerId = ((HarvestProductDto) product).getProducer().getId();
				trader = createdProducers.stream().filter(p -> p.getId().equals(producerId))
						.findFirst().orElse(null);
				if (trader == null) { // Fallback if producer not found in list
					log.warn(
							"Producer {} for HarvestProduct {} not found in created list, selecting random producer.",
							producerId, product.getId());
					if (createdProducers.isEmpty()) continue; // Skip if no producers exist at all
					trader = createdProducers.get(random.nextInt(createdProducers.size()));
				}
			} else if (product instanceof TransformedProductDto) {
				Integer transformerId = ((TransformedProductDto) product).getTransformer().getId();
				trader = createdTransformers.stream().filter(t -> t.getId().equals(transformerId))
						.findFirst().orElse(null);
				if (trader == null) {
					log.warn(
							"Transformer {} for TransformedProduct {} not found in created list, selecting random transformer.",
							transformerId, product.getId());
					if (createdTransformers.isEmpty()) continue; // Skip if no transformers exist
					trader = createdTransformers.get(random.nextInt(createdTransformers.size()));
				}
			} else {
				log.warn("Unknown product type for auction creation: {}",
						product.getClass().getSimpleName());
				// Fallback: Assign a random trader? Or skip?
				if (allTraders.isEmpty()) continue;
				trader = allTraders.get(random.nextInt(allTraders.size()));
			}

			// Generate dates
			Date randomDate = faker.date().between(Date.from(startOfYear), Date.from(endOfYear));
			LocalDateTime creationDate = LocalDateTime.ofInstant(randomDate.toInstant(),
					ZoneId.systemDefault());
			LocalDateTime expirationDate = creationDate
					.plusDays(faker.number().numberBetween(5, 45));

			// Determine Status based on creationDate vs now
			TradeStatusDto status;
			boolean isActive;
			if (creationDate.isAfter(generationTime)) {
				status = statusOpen; // Future auction
				isActive = true;
			} else {
				// Past auction - choose a finished status
				int statusChoice = random.nextInt(10); // 0-9
				if (statusChoice < 5)
					status = statusAccepted; // 50% chance Accepted
				else if (statusChoice < 8)
					status = statusExpired; // 30% chance Expired
				else status = statusConcluded; // 20% chance Concluded
				isActive = false; // Finished auctions are inactive
			}

			AuctionUpdateDto auctionDto = new AuctionUpdateDto();
			auctionDto.setProductId(product.getId());
			auctionDto.setTraderId(trader.getId());
			auctionDto.setPrice(generateRandomPrice());
			auctionDto.setProductQuantity(faker.number().numberBetween(5, 500)); // Example quantity
			auctionDto.setActive(isActive);
			auctionDto.setCreationDate(creationDate);
			auctionDto.setExpirationDate(expirationDate);
			auctionDto.setStatusId(status.getId());
			auctionDto.setOptions(new AuctionOptionsUpdateDto()); // Use default strategy

			try {
				createdAuctions.add(auctionService.createAuction(auctionDto));
			} catch (Exception e) {
				log.error("Failed to create auction for trader {}: {}", trader.getId(),
						e.getMessage());
				// Potential issues: constraint violations, null pointers if data inconsistent
			}
		}
		log.info("Auctions created (Total: {}).", createdAuctions.size());
	}

	// --- Step 9: Bids ---
	private void createBidsForFinishedAuctions() {
		log.info("Creating Bids on all Auctions…");

		// 1) Candidats à l’enchère ------------------------------------------------
		List<TraderDetailDto> potentialBidders = new ArrayList<>();
		potentialBidders.addAll(createdProducers);
		potentialBidders.addAll(createdTransformers);
		potentialBidders.addAll(createdExporters);

		if (potentialBidders.isEmpty()) {
			log.warn("No potential bidders available.");
			return;
		}

		int bidsCreatedCount = 0;

		// 2) Parcours de chaque vente --------------------------------------------
		for (AuctionDto auction : createdAuctions) {

			/* nombre d'offres à créer pour cette auction */
			int numBids = random
					.nextInt(MAX_BIDS_PER_FINISHED_AUCTION - MIN_BIDS_PER_FINISHED_AUCTION + 1)
					+ MIN_BIDS_PER_FINISHED_AUCTION;
			if (numBids == 0) continue;

			/* on exclut le propriétaire de la vente des enchérisseurs */
			final Integer ownerId = auction.getTrader().getId();
			List<TraderDetailDto> bidders = potentialBidders.stream()
					.filter(t -> !t.getId().equals(ownerId)).toList();
			if (bidders.isEmpty()) continue;

			boolean auctionFinished = !auction.getStatus().getId().equals(statusOpen.getId());

			BigDecimal currentHighest = BigDecimal.valueOf(auction.getPrice());
			LocalDateTime lastBidTime = auction.getCreationDate();

			List<BidUpdateDto> bids = new ArrayList<>();

			// 3) Génération des bids ---------------------------------------------
			for (int i = 0; i < numBids; i++) {

				TraderDetailDto bidder = bidders.get(random.nextInt(bidders.size()));

				/* fenêtre temporelle pour la date d'offre */
				LocalDateTime maxBidTime = auction.getExpirationDate().isBefore(generationTime)
						? auction.getExpirationDate()
						: generationTime;

				if (lastBidTime.isAfter(maxBidTime.minusMinutes(1))) {
					lastBidTime = maxBidTime.minusMinutes(1);
				}
				if (lastBidTime.equals(maxBidTime)) break;

				Date bidDate = faker.date().between(
						Date.from(lastBidTime.plusSeconds(1).atZone(ZoneId.systemDefault())
								.toInstant()),
						Date.from(maxBidTime.atZone(ZoneId.systemDefault()).toInstant()));
				LocalDateTime bidCreationDate = LocalDateTime.ofInstant(bidDate.toInstant(),
						ZoneId.systemDefault());
				lastBidTime = bidCreationDate;

				/* montant = +0-15 % au-dessus de l'offre courante */
				BigDecimal amount = currentHighest
						.multiply(BigDecimal.valueOf(1 + random.nextDouble() * 0.15))
						.setScale(2, RoundingMode.HALF_UP);
				currentHighest = amount;

				BidUpdateDto bid = new BidUpdateDto();
				bid.setAuctionId(auction.getId());
				bid.setTraderId(bidder.getId());
				bid.setAmount(amount);
				bid.setCreationDate(bidCreationDate);

				// statut provisoire : Ouvert si l'auction l'est encore, sinon Expiré
				bid.setStatusId(auctionFinished ? statusRejected.getId() : statusOpen.getId());

				bids.add(bid);
			}

			// 4) Si la vente est terminée, on accepte la meilleure enchère --------
			if (auctionFinished && !bids.isEmpty()) {
				BidUpdateDto winner = bids.stream()
						.max(Comparator.comparing(BidUpdateDto::getAmount)).orElseThrow();
				winner.setStatusId(statusAccepted.getId());
			}

			// 5) Persistance ------------------------------------------------------
			for (BidUpdateDto dto : bids) {
				try {
					bidService.createBid(dto);
					bidsCreatedCount++;
				} catch (Exception e) {
					log.error("Failed to create bid for auction {}: {}", auction.getId(),
							e.getMessage());
				}
			}
		}
		log.info("Bids created (Total: {}).", bidsCreatedCount);
	}

	// --- Helper Methods ---

	private double generateRandomPrice() {
		return faker.number().randomDouble(2, 10, 10000);
	}

	/** Génère une adresse email unique pour tout le run */
	private String uniqueEmail() {
		String email;
		do {
			email = faker.internet().emailAddress(); // ou safeEmailAddress()
		} while (!generatedEmails.add(email)); // add() renvoie false si déjà présent
		return email;
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

	private Point createRandomPointInCity() {
		// Si pas de villes en base, génère une coordonnée aléatoire
		if (cities == null || cities.isEmpty()) {
			double lat = parseDoubleSafe(faker.address().latitude());
			double lon = parseDoubleSafe(faker.address().longitude());
			return geometryFactory.createPoint(new Coordinate(lon, lat));
		}

		// Sinon, on pioche une ville existante et on retourne son point
		City randomCity = cities.get(ThreadLocalRandom.current().nextInt(cities.size()));
		return randomCity.getLocation();
	}

	/** Construit un WKT POINT(longitude latitude) au format décimal US (séparateur '.') */
	private String randomWktPoint() {
		double lon = parseDoubleSafe(faker.address().longitude());
		double lat = parseDoubleSafe(faker.address().latitude());
		return String.format(Locale.US, "POINT(%f %f)", lon, lat);
	}

	/** Prix au km aléatoire entre 0.50 € et 5.00 € */
	private BigDecimal randomPricePerKm() {
		return BigDecimal.valueOf(faker.number().randomDouble(2, 50, 500))
				.divide(BigDecimal.valueOf(100));
	}

	private double parseDoubleSafe(String coord) {
		if (coord == null) {
			throw new IllegalArgumentException("Coordonnée invalide : null");
		}
		// remplace la virgule par un point si nécessaire
		coord = coord.replace(',', '.');
		try {
			return Double.parseDouble(coord);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Impossible de parser la coordonnée « " + coord + " »", e);
		}
	}

	private TradeStatusDto createTradeStatusDto(String name) {
		TradeStatusDto status = new TradeStatusDto();
		status.setName(name);
		return status;
	}

	// --- User DTO Creation Helpers ---

	private AdminUpdateDto createRandomAdminDto() {
		AdminUpdateDto admin = new AdminUpdateDto();
		admin.setFirstName(faker.name().firstName());
		admin.setLastName(faker.name().lastName());
		admin.setEmail(uniqueEmail());
		admin.setPassword(DEFAULT_PASSWORD);
		Region randomRegion = regions.get(faker.number().numberBetween(0, regions.size()));
		List<City> citiesInRegion = cities.stream().filter(c -> c.getRegion().equals(randomRegion))
				.collect(Collectors.toList());
		if (citiesInRegion.isEmpty()) {
			citiesInRegion = List.copyOf(cities);
		}
		City randomCity = citiesInRegion
				.get(faker.number().numberBetween(0, citiesInRegion.size()));
		AddressDto addr = AddressDto.builder().street(faker.address().streetAddress())
				.location(randomWktPoint()).regionId(randomRegion.getId())
				.cityId(randomCity.getId()).build();
		admin.setAddress(addr);
		admin.setEnabled(true);
		admin.setRegistrationDate(generateRandomDateTimeInPast());
		admin.setValidationDate(
				admin.getRegistrationDate().plusDays(faker.number().numberBetween(1, 5))); // Validated
		// shortly
		// after
		// registration
		admin.setPhone(faker.phoneNumber().cellPhone());
		admin.setLanguageId(langFr.getId()); // Default to French for now
		return admin;
	}

	private ProducerUpdateDto createRandomProducerDto() {
		ProducerUpdateDto producer = new ProducerUpdateDto();
		producer.setFirstName(faker.name().firstName());
		producer.setLastName(faker.name().lastName());
		producer.setEmail(uniqueEmail());
		producer.setPassword(DEFAULT_PASSWORD);
		producer.setEnabled(true);
		Region randomRegion = regions.get(faker.number().numberBetween(0, regions.size()));
		List<City> citiesInRegion = cities.stream().filter(c -> c.getRegion().equals(randomRegion))
				.collect(Collectors.toList());
		if (citiesInRegion.isEmpty()) {
			citiesInRegion = List.copyOf(cities);
		}
		City randomCity = citiesInRegion
				.get(faker.number().numberBetween(0, citiesInRegion.size()));
		AddressDto addr = AddressDto.builder().street(faker.address().streetAddress())
				.location(randomWktPoint()).regionId(randomRegion.getId())
				.cityId(randomCity.getId()).build();
		producer.setAddress(addr);
		producer.setRegistrationDate(generateRandomDateTimeInPast());
		producer.setValidationDate(
				producer.getRegistrationDate().plusDays(faker.number().numberBetween(1, 10)));
		producer.setPhone(faker.phoneNumber().cellPhone());
		producer.setAgriculturalIdentifier(faker.number().digits(9));
		producer.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return producer;
	}

	private TransformerUpdateDto createRandomTransformerDto() {
		TransformerUpdateDto transformer = new TransformerUpdateDto();
		transformer.setFirstName(faker.name().firstName());
		transformer.setLastName(faker.name().lastName());
		transformer.setEmail(uniqueEmail());
		transformer.setPassword(DEFAULT_PASSWORD);
		transformer.setEnabled(true);
		Region randomRegion = regions.get(faker.number().numberBetween(0, regions.size()));
		List<City> citiesInRegion = cities.stream().filter(c -> c.getRegion().equals(randomRegion))
				.collect(Collectors.toList());
		if (citiesInRegion.isEmpty()) {
			citiesInRegion = List.copyOf(cities);
		}
		City randomCity = citiesInRegion
				.get(faker.number().numberBetween(0, citiesInRegion.size()));
		AddressDto addr = AddressDto.builder().street(faker.address().streetAddress())
				.location(randomWktPoint()).regionId(randomRegion.getId())
				.cityId(randomCity.getId()).build();
		transformer.setAddress(addr);
		transformer.setRegistrationDate(generateRandomDateTimeInPast());
		transformer.setValidationDate(
				transformer.getRegistrationDate().plusDays(faker.number().numberBetween(1, 10)));
		transformer.setPhone(faker.phoneNumber().cellPhone());
		transformer.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return transformer;
	}

	private ExporterUpdateDto createRandomExporterDto() {
		ExporterUpdateDto exporter = new ExporterUpdateDto();
		exporter.setFirstName(faker.name().firstName());
		exporter.setLastName(faker.name().lastName());
		exporter.setEmail(uniqueEmail());
		exporter.setPassword(DEFAULT_PASSWORD);
		exporter.setEnabled(true);
		Region randomRegion = regions.get(faker.number().numberBetween(0, regions.size()));
		List<City> citiesInRegion = cities.stream().filter(c -> c.getRegion().equals(randomRegion))
				.collect(Collectors.toList());
		if (citiesInRegion.isEmpty()) {
			citiesInRegion = List.copyOf(cities);
		}
		City randomCity = citiesInRegion
				.get(faker.number().numberBetween(0, citiesInRegion.size()));
		AddressDto addr = AddressDto.builder().street(faker.address().streetAddress())
				.location(randomWktPoint()).regionId(randomRegion.getId())
				.cityId(randomCity.getId()).build();
		exporter.setAddress(addr);
		exporter.setRegistrationDate(generateRandomDateTimeInPast());
		exporter.setValidationDate(
				exporter.getRegistrationDate().plusDays(faker.number().numberBetween(1, 10)));
		exporter.setPhone(faker.phoneNumber().cellPhone());
		exporter.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return exporter;
	}

	private CarrierUpdateDto createRandomCarrierDto() {
		CarrierUpdateDto carrier = new CarrierUpdateDto();
		carrier.setFirstName(faker.name().firstName());
		carrier.setLastName(faker.name().lastName());
		carrier.setEmail(uniqueEmail());
		carrier.setPassword(DEFAULT_PASSWORD);
		carrier.setEnabled(true);
		Region randomRegion = regions.get(faker.number().numberBetween(0, regions.size()));
		List<City> citiesInRegion = cities.stream().filter(c -> c.getRegion().equals(randomRegion))
				.collect(Collectors.toList());
		if (citiesInRegion.isEmpty()) {
			citiesInRegion = List.copyOf(cities);
		}
		City randomCity = citiesInRegion
				.get(faker.number().numberBetween(0, citiesInRegion.size()));
		AddressDto addr = AddressDto.builder().street(faker.address().streetAddress())
				.location(randomWktPoint()).regionId(randomRegion.getId())
				.cityId(randomCity.getId()).build();
		carrier.setAddress(addr);
		carrier.setPricePerKm(randomPricePerKm());
		carrier.setRegistrationDate(generateRandomDateTimeInPast());
		carrier.setValidationDate(
				carrier.getRegistrationDate().plusDays(faker.number().numberBetween(1, 10)));
		carrier.setPhone(faker.phoneNumber().cellPhone());
		carrier.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return carrier;
	}

	private QualityInspectorUpdateDto createRandomQualityInspectorDto() {
		QualityInspectorUpdateDto qualityInspector = new QualityInspectorUpdateDto();
		qualityInspector.setFirstName(faker.name().firstName());
		qualityInspector.setLastName(faker.name().lastName());
		qualityInspector.setEmail(uniqueEmail());
		qualityInspector.setPassword(DEFAULT_PASSWORD);
		qualityInspector.setEnabled(true);
		Region randomRegion = regions.get(faker.number().numberBetween(0, regions.size()));
		List<City> citiesInRegion = cities.stream().filter(c -> c.getRegion().equals(randomRegion))
				.collect(Collectors.toList());
		if (citiesInRegion.isEmpty()) {
			citiesInRegion = List.copyOf(cities);
		}
		City randomCity = citiesInRegion
				.get(faker.number().numberBetween(0, citiesInRegion.size()));
		AddressDto addr = AddressDto.builder().street(faker.address().streetAddress())
				.location(randomWktPoint()).regionId(randomRegion.getId())
				.cityId(randomCity.getId()).build();
		qualityInspector.setAddress(addr);
		qualityInspector.setRegistrationDate(generateRandomDateTimeInPast());
		qualityInspector.setValidationDate(qualityInspector.getRegistrationDate()
				.plusDays(faker.number().numberBetween(1, 10)));
		qualityInspector.setPhone(faker.phoneNumber().cellPhone());
		qualityInspector.setLanguageId(random.nextBoolean() ? langFr.getId() : langEn.getId());
		return qualityInspector;
	}
}