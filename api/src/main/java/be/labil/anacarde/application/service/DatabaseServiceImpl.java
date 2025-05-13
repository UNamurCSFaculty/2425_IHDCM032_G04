package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.*;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.db.user.QualityInspectorDetailDto;
import be.labil.anacarde.domain.dto.db.user.TraderDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.dto.write.CooperativeUpdateDto;
import be.labil.anacarde.domain.dto.write.QualityControlUpdateDto;
import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.ProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.TransformedProductUpdateDto;
import be.labil.anacarde.domain.dto.write.user.*;
import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.domain.model.Region;
import be.labil.anacarde.infrastructure.import_data.RegionCityImportService;
import be.labil.anacarde.infrastructure.persistence.*;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

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
	private final ContractOfferRepository contractOfferRepository;
	private final QualityRepository qualityRepository;
	private final QualityControlRepository qualityControlRepository;

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
	private final QualityService qualityService;
	private final QualityControlService qualityControlService;

	private final RegionRepository regionRepository;
	private final CityRepository cityRepository;
	private final EntityManager entityManager;
	private final RegionCityImportService regionCityImportService;

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
	public void createDatabase() {
		// Langue
		LanguageDto languageFr = LanguageDto.builder().name("Français").code("fr").build();
		languageFr = languageService.createLanguage(languageFr);

		LanguageDto languageEn = LanguageDto.builder().name("English").code("en").build();
		languageEn = languageService.createLanguage(languageEn);

		// Ajout des régions et villes
		List<Region> regions = List.of();
		List<City> cities = List.of();
		try {
			Map<Integer, Region> regs = regionCityImportService.importRegions();
			regions = regs.values().stream().toList();
			cities = regionCityImportService.importCities("/data/cities.json", regs);
			entityManager.flush();
		} catch (Exception e) {
			System.out
					.println("Erreur lors de la création des régions et villes: " + e.getMessage());
			return;
		}

		// 1️⃣ Récupérer une ville “par défaut” pour tous les exemples d’adresses
		City defaultCity = cityRepository.findByName("Cotonou")
				.orElseGet(() -> cityRepository.findAll().stream().findFirst()
						.orElseThrow(() -> new IllegalStateException("Aucune ville trouvée")));
		Integer defaultCityId = defaultCity.getId();
		Integer defaultRegionId = defaultCity.getRegion().getId();

		getRandomEl(regions);

		// Création du producteur (sans coopérative) et d'un champ
		UserUpdateDto producerUpdate = createProducer(languageFr, getRandomEl(regions),
				getRandomEl(cities));
		UserDetailDto producer = userService.createUser(producerUpdate);
		FieldDto field = createField((ProducerDetailDto) producer);
		field = fieldService.createField(field);

		// Création de la coopérative
		CooperativeUpdateDto cooperativeUpdateDto = createCooperative((ProducerDetailDto) producer);
		CooperativeDto cooperativeDto = cooperativeService.createCooperative(cooperativeUpdateDto);

		// Ajout du producteur à la coopérative
		((ProducerDetailDto) producer).setCooperative(cooperativeDto);
		producerUpdate.setPassword(null); // on ne veut pas que le mot de passe soit mis à jour
		producer = userService.updateUser(producer.getId(), producerUpdate);

		// Création de l'utilisateur admin
		UserUpdateDto adminUpdate = createAdmin(languageFr);
		UserDetailDto admin = userService.createUser(adminUpdate);

		// Création de l'utilisateur exportateur
		UserUpdateDto exporterUpdate = createExporter(languageFr);
		UserDetailDto exporter = userService.createUser(exporterUpdate);

		// Création de l'utilisateur transformateur
		UserUpdateDto transformerUpdate = createTransformer(languageFr);
		UserDetailDto transformer = userService.createUser(transformerUpdate);

		// Création de l'utilisateur qualiticien
		UserUpdateDto qualityInspectorUpdate = createQualityInspector(languageFr);
		UserDetailDto qualityInspector = userService.createUser(qualityInspectorUpdate);

		// Création d'un store
		StoreDetailDto store = createStore(admin);
		store = storeService.createStore(store);

		// Création d'une qualité
		QualityDto quality = createQuality("WW10");
		quality = qualityService.createQuality(quality);

		QualityDto quality2 = createQuality("AA20");
		quality2 = qualityService.createQuality(quality2);

		QualityDto quality3 = createQuality("TP570");
		quality3 = qualityService.createQuality(quality3);

		// Création d'un contrôle qualité
		QualityControlUpdateDto qualityControlUpdate = createQualityControl(quality,
				(QualityInspectorDetailDto) qualityInspector);
		QualityControlDto qualityControl = qualityControlService
				.createQualityControl(qualityControlUpdate);

		QualityControlUpdateDto qualityControlUpdate2 = createQualityControl(quality2,
				(QualityInspectorDetailDto) qualityInspector);
		QualityControlDto qualityControl2 = qualityControlService
				.createQualityControl(qualityControlUpdate2);

		QualityControlUpdateDto qualityControlUpdate3 = createQualityControl(quality3,
				(QualityInspectorDetailDto) qualityInspector);
		QualityControlDto qualityControl3 = qualityControlService
				.createQualityControl(qualityControlUpdate3);

		// Création de produits
		ProductUpdateDto productUpdate = createHarvestProduct(store, producer, field,
				qualityControl, 1000);
		ProductDto product = productService.createProduct(productUpdate);

		ProductUpdateDto product2Update = createHarvestProduct(store, producer, field,
				qualityControl2, 2000);
		ProductDto product2 = productService.createProduct(product2Update);

		ProductUpdateDto transformedProductUpdate = createTransformedProduct(store, transformer,
				qualityControl3, 500);
		ProductDto transformedProduct = productService.createProduct(transformedProductUpdate);

		// Création d'une stratégie d'enchère
		AuctionStrategyDto auctionStrategy = new AuctionStrategyDto();
		auctionStrategy.setName("Stratégie de test");
		auctionStrategy = auctionStrategyService.createAuctionStrategy(auctionStrategy);

		// Création de status (enchères et offres)
		TradeStatusDto tradeStatusOpen = createTradeStatus("Ouvert");
		TradeStatusDto tradeStatusExpired = createTradeStatus("Expiré");
		TradeStatusDto tradeStatusAccepted = createTradeStatus("Accepté");
		TradeStatusDto tradeStatusRejected = createTradeStatus("Refusé");
		tradeStatusOpen = tradeStatusService.createTradeStatus(tradeStatusOpen);
		tradeStatusExpired = tradeStatusService.createTradeStatus(tradeStatusExpired);
		tradeStatusAccepted = tradeStatusService.createTradeStatus(tradeStatusAccepted);
		tradeStatusRejected = tradeStatusService.createTradeStatus(tradeStatusRejected);

		// Création d'enchères (pour l'utilisateur producer)
		AuctionUpdateDto createAuction1 = createAuction(product, (TraderDetailDto) producer,
				BigDecimal.valueOf(500), 10, LocalDateTime.now(), auctionStrategy, tradeStatusOpen);
		AuctionUpdateDto createAuction2 = createAuction(product2, (TraderDetailDto) producer,
				BigDecimal.valueOf(2500), 20, LocalDateTime.now().plusDays(5), auctionStrategy,
				tradeStatusOpen);
		AuctionUpdateDto createuction3 = createAuction(transformedProduct,
				(TraderDetailDto) producer, BigDecimal.valueOf(3500), 50,
				LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusOpen);
		AuctionUpdateDto createuction4 = createAuction(product2, (TraderDetailDto) producer,
				BigDecimal.valueOf(777), 50, LocalDateTime.now().plusDays(5), auctionStrategy,
				tradeStatusExpired);
		AuctionDto auction1 = auctionService.createAuction(createAuction1);
		AuctionDto auction2 = auctionService.createAuction(createAuction2);
		AuctionDto auction3 = auctionService.createAuction(createuction3);
		AuctionDto auction4 = auctionService.createAuction(createuction4);

		// Création d'enchères (pour l'utilisateur transformateur)
		AuctionUpdateDto createAuction5 = createAuction(product, (TraderDetailDto) transformer,
				BigDecimal.valueOf(999), 100, LocalDateTime.now().plusDays(5), auctionStrategy,
				tradeStatusOpen);
		AuctionDto auction5 = auctionService.createAuction(createAuction5);

		// Création d'offres
		BidUpdateDto bid1 = createBid(auction1, (TraderDetailDto) transformer,
				BigDecimal.valueOf(100), LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid2 = createBid(auction1, (TraderDetailDto) transformer,
				BigDecimal.valueOf(200), LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid3 = createBid(auction1, (TraderDetailDto) transformer,
				BigDecimal.valueOf(300), LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid4 = createBid(auction1, (TraderDetailDto) transformer,
				BigDecimal.valueOf(500), LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid5 = createBid(auction1, (TraderDetailDto) transformer,
				BigDecimal.valueOf(600), LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid6 = createBid(auction2, (TraderDetailDto) transformer,
				BigDecimal.valueOf(10), LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid7 = createBid(auction2, (TraderDetailDto) transformer,
				BigDecimal.valueOf(20), LocalDateTime.now(), tradeStatusOpen);
		bidService.createBid(bid1);
		bidService.createBid(bid2);
		bidService.createBid(bid3);
		bidService.createBid(bid4);
		bidService.createBid(bid5);
		bidService.createBid(bid6);
		bidService.createBid(bid7);

		entityManager.flush();

		System.out.println("🧪 Test data loaded (profile=dev)");
	}

	private CooperativeUpdateDto createCooperative(ProducerDetailDto producer) {
		CooperativeUpdateDto cooperativeDto = new CooperativeUpdateDto();
		cooperativeDto.setName("Cooperative de test");
		cooperativeDto.setCreationDate(LocalDateTime.now().minusDays(30));
		cooperativeDto.setPresidentId(producer.getId());
		return cooperativeDto;
	}

	private FieldDto createField(ProducerDetailDto producer) {
		FieldDto fieldDto = new FieldDto();
		fieldDto.setAddress(
				AddressDto.builder().street("Rue de la paix").cityId(1).regionId(1).build());
		fieldDto.setIdentifier("F12748");
		fieldDto.setProducer(producer);
		return fieldDto;
	}

	private HarvestProductUpdateDto createHarvestProduct(StoreDetailDto store,
			UserDetailDto producer, FieldDto field, QualityControlDto qualityControl,
			double weight) {
		HarvestProductUpdateDto harvestProduct = new HarvestProductUpdateDto();
		harvestProduct.setProducerId(producer.getId());
		harvestProduct.setStoreId(store.getId());
		harvestProduct.setWeightKg(weight);
		harvestProduct.setDeliveryDate(LocalDateTime.now());
		harvestProduct.setFieldId(field.getId());
		harvestProduct.setQualityControlId(qualityControl.getId());
		return harvestProduct;
	}

	private TransformedProductUpdateDto createTransformedProduct(StoreDetailDto store,
			UserDetailDto producer, QualityControlDto qualityControl, double weight) {
		TransformedProductUpdateDto transformedProduct = new TransformedProductUpdateDto();
		transformedProduct.setIdentifier("ID-Transformed-20402");
		transformedProduct.setTransformerId(producer.getId());
		transformedProduct.setStoreId(store.getId());
		transformedProduct.setWeightKg(weight);
		transformedProduct.setDeliveryDate(LocalDateTime.now());
		transformedProduct.setQualityControlId(qualityControl.getId());
		return transformedProduct;
	}

	private AuctionUpdateDto createAuction(ProductDto product, TraderDetailDto trader,
			BigDecimal price, int quantity, LocalDateTime date, AuctionStrategyDto strategy,
			TradeStatusDto status) {
		AuctionUpdateDto auction = new AuctionUpdateDto();
		auction.setProductId(product.getId());
		auction.setPrice(price);
		auction.setActive(true);
		auction.setCreationDate(date);
		auction.setExpirationDate(date.plusDays(20));
		auction.setProductQuantity(quantity);
		auction.setStrategyId(strategy.getId());
		auction.setTraderId(trader.getId());
		auction.setStatusId(status.getId());
		return auction;
	}

	private TradeStatusDto createTradeStatus(String name) {
		TradeStatusDto status = new TradeStatusDto();
		status.setName(name);
		return status;
	}

	private BidUpdateDto createBid(AuctionDto auction, TraderDetailDto trader, BigDecimal amount,
			LocalDateTime creationDate, TradeStatusDto status) {
		BidUpdateDto bid = new BidUpdateDto();
		bid.setAuctionId(auction.getId());
		bid.setTraderId(trader.getId());
		bid.setAmount(amount);
		bid.setCreationDate(creationDate);
		bid.setStatusId(status.getId());
		return bid;
	}

	private StoreDetailDto createStore(UserDetailDto manager) {
		StoreDetailDto store = new StoreDetailDto();
		store.setName("Nassara");
		store.setUserId(manager.getId());
		store.setAddress(AddressDto.builder().street("Rue du Store").cityId(1).regionId(1).build());
		return store;
	}

	private QualityDto createQuality(String qualityName) {
		QualityDto quality = new QualityDto();
		quality.setName(qualityName);
		return quality;
	}

	private QualityControlUpdateDto createQualityControl(QualityDto quality,
			QualityInspectorDetailDto qualityInspector) {
		QualityControlUpdateDto qualityControl = new QualityControlUpdateDto();
		qualityControl.setIdentifier("QC-001");
		qualityControl.setQualityId(quality.getId());
		qualityControl.setQualityInspectorId(qualityInspector.getId());
		qualityControl.setHumidity(12.0f);
		qualityControl.setGranularity(5.0f);
		qualityControl.setKorTest(89.0f);
		qualityControl.setControlDate(LocalDateTime.now());

		return qualityControl;
	}

	private AdminUpdateDto createAdmin(LanguageDto languageDto) {
		AdminUpdateDto admin = new AdminUpdateDto();
		admin.setFirstName("John");
		admin.setLastName("Doe");
		admin.setEmail("johndoe@gmail.com");
		admin.setPassword("azertyui");
		admin.setAddress(AddressDto.builder().street("Rue Admin").cityId(1).regionId(1).build());
		admin.setEnabled(true);
		admin.setEnabled(true);
		admin.setRegistrationDate(LocalDateTime.now());
		admin.setValidationDate(LocalDateTime.now());
		admin.setPhone("+2290197000002");
		admin.setLanguageId(languageDto.getId());
		return admin;
	}

	private ProducerUpdateDto createProducer(LanguageDto languageDto, Region region, City city) {
		ProducerUpdateDto producer = new ProducerUpdateDto();
		producer.setFirstName("Fabrice");
		producer.setLastName("Producer");
		producer.setEmail("fabricecipolla@gmail.com");
		producer.setPassword("azertyui");
		producer.setEnabled(true);
		producer.setAddress(AddressDto.builder().street("Rue Producer").cityId(city.getId())
				.regionId(region.getId()).build());
		producer.setRegistrationDate(LocalDateTime.now());
		producer.setValidationDate(LocalDateTime.now());
		producer.setPhone("+2290197000000");
		producer.setAgriculturalIdentifier("123456789");
		producer.setLanguageId(languageDto.getId());
		return producer;
	}

	private ExporterUpdateDto createExporter(LanguageDto languageDto) {
		ExporterUpdateDto exporter = new ExporterUpdateDto();
		exporter.setFirstName("Stéphane");
		exporter.setLastName("Exporter");
		exporter.setEmail("stephaneglibert@gmail.com");
		exporter.setPassword("azertyui");
		exporter.setAddress(
				AddressDto.builder().street("Rue Exporter").cityId(1).regionId(1).build());
		exporter.setEnabled(true);
		exporter.setRegistrationDate(LocalDateTime.now());
		exporter.setValidationDate(LocalDateTime.now());
		exporter.setPhone("+2290197000001");
		exporter.setLanguageId(languageDto.getId());
		return exporter;
	}

	private TransformerUpdateDto createTransformer(LanguageDto languageDto) {
		TransformerUpdateDto transformer = new TransformerUpdateDto();
		transformer.setFirstName("Homer");
		transformer.setLastName("Transformer");
		transformer.setEmail("homer@gmail.com");
		transformer.setPassword("azertyui");
				AddressDto.builder().street("Rue Transformer").cityId(1).regionId(1).build());
		transformer.setAddress(
		transformer.setEnabled(true);
		transformer.setRegistrationDate(LocalDateTime.now());
		transformer.setValidationDate(LocalDateTime.now());
		transformer.setPhone("+22944223201");
		transformer.setLanguageId(languageDto.getId());
		return transformer;
	}

	private QualityInspectorUpdateDto createQualityInspector(LanguageDto languageDto) {
		QualityInspectorUpdateDto qualityInspector = new QualityInspectorUpdateDto();
		qualityInspector.setFirstName("Bart");
		qualityInspector.setLastName("QualityInspector");
		qualityInspector.setEmail("bart@gmail.com");
		qualityInspector.setPassword("azertyui");
		qualityInspector.setAddress("Springfield");
		qualityInspector.setEnabled(true);
		qualityInspector.setRegistrationDate(LocalDateTime.now());
		qualityInspector.setValidationDate(LocalDateTime.now());
		qualityInspector.setPhone("+22944225551");
		qualityInspector.setLanguageId(languageDto.getId());
		return qualityInspector;
	}
}
