package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.db.*;
import be.labil.anacarde.domain.dto.db.product.ProductDto;
import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.db.user.TraderDetailDto;
import be.labil.anacarde.domain.dto.db.user.UserDetailDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.dto.write.CooperativeUpdateDto;
import be.labil.anacarde.domain.dto.write.product.HarvestProductUpdateDto;
import be.labil.anacarde.domain.dto.write.product.ProductUpdateDto;
import be.labil.anacarde.domain.dto.write.user.*;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.infrastructure.persistence.*;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
	private final RegionRepository regionRepository;
	private final DocumentRepository documentRepository;
	private final QualityRepository qualityRepository;
	private final ContractOfferRepository contractOfferRepository;
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

	private final EntityManager entityManager;

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
		qualityControlRepository.deleteAllInBatch();
		cooperativeRepository.deleteAllInBatch();
		contractOfferRepository.deleteAllInBatch();
		qualityRepository.deleteAllInBatch();
		documentRepository.deleteAllInBatch();
		bidRepository.deleteAllInBatch();
		auctionRepository.deleteAllInBatch();
		tradeStatusRepository.deleteAllInBatch();
		auctionStrategyRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		fieldRepository.deleteAllInBatch();
		storeRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		roleRepository.deleteAllInBatch();
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

		// Création du producteur (sans coopérative) et d'un champ
		UserUpdateDto producerUpdate = createProducer(languageFr);
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

		// Création d'un store
		StoreDetailDto store = createStore(admin);
		store = storeService.createStore(store);

		// Création d'un produit
		ProductUpdateDto productUpdate = createHarvestProduct(store, producer, field, 1000);
		ProductDto product = productService.createProduct(productUpdate);

		ProductUpdateDto product2Update = createHarvestProduct(store, producer, field, 2000);
		ProductDto product2 = productService.createProduct(product2Update);

		// Création d'une stratégie d'enchère
		AuctionStrategyDto auctionStrategy = new AuctionStrategyDto();
		auctionStrategy.setName("Stratégie de test");
		auctionStrategy = auctionStrategyService.createAuctionStrategy(auctionStrategy);

		// Création de status (enchères et offres)
		TradeStatusDto tradeStatusOpen = createTradeStatus("Ouvert");
		TradeStatusDto tradeStatusExpired = createTradeStatus("Expiré");
		TradeStatusDto tradeStatusAccepted = createTradeStatus("Accepté");
		tradeStatusOpen = tradeStatusService.createTradeStatus(tradeStatusOpen);
		tradeStatusExpired = tradeStatusService.createTradeStatus(tradeStatusExpired);
		tradeStatusAccepted = tradeStatusService.createTradeStatus(tradeStatusAccepted);

		// Création d'enchères (pour l'utilisateur producer)
		AuctionUpdateDto createAuction1 = createAuction(product, (TraderDetailDto) producer, BigDecimal.valueOf(500),
				10, LocalDateTime.now(), auctionStrategy, tradeStatusOpen);
		AuctionUpdateDto createAuction2 = createAuction(product, (TraderDetailDto) producer, BigDecimal.valueOf(2500),
				20, LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusOpen);
		AuctionUpdateDto createuction3 = createAuction(product, (TraderDetailDto) producer, BigDecimal.valueOf(3500),
				50, LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusOpen);
		AuctionUpdateDto createuction4 = createAuction(product, (TraderDetailDto) producer, BigDecimal.valueOf(777), 50,
				LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusExpired);
		AuctionDto auction1 = auctionService.createAuction(createAuction1);
		AuctionDto auction2 = auctionService.createAuction(createAuction2);
		AuctionDto auction3 = auctionService.createAuction(createuction3);
		AuctionDto auction4 = auctionService.createAuction(createuction4);

		// Création d'enchères (pour l'utilisateur transformateur)
		AuctionUpdateDto createAuction5 = createAuction(product, (TraderDetailDto) transformer, BigDecimal.valueOf(999),
				100, LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusOpen);
		AuctionDto auction5 = auctionService.createAuction(createAuction5);

		// Création d'offres
		BidUpdateDto bid1 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(100),
				LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid2 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(200),
				LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid3 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(300),
				LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid4 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(500),
				LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid5 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(600),
				LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid6 = createBid(auction2, (TraderDetailDto) transformer, BigDecimal.valueOf(10),
				LocalDateTime.now(), tradeStatusOpen);
		BidUpdateDto bid7 = createBid(auction2, (TraderDetailDto) transformer, BigDecimal.valueOf(20),
				LocalDateTime.now(), tradeStatusOpen);
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
		cooperativeDto.setAddress("Adresse de la cooperative");
		cooperativeDto.setPresidentId(producer.getId());
		return cooperativeDto;
	}

	private FieldDto createField(ProducerDetailDto producer) {
		FieldDto fieldDto = new FieldDto();
		fieldDto.setLocation("POINT (2.3522 48.8566)");
		fieldDto.setIdentifier("F12748");
		fieldDto.setProducer(producer);
		return fieldDto;
	}

	private HarvestProductUpdateDto createHarvestProduct(StoreDetailDto store, UserDetailDto producer, FieldDto field,
			double weight) {
		HarvestProductUpdateDto harvestProduct = new HarvestProductUpdateDto();
		harvestProduct.setProducerId(producer.getId());
		harvestProduct.setStoreId(store.getId());
		harvestProduct.setWeightKg(weight);
		harvestProduct.setDeliveryDate(LocalDateTime.now());
		harvestProduct.setFieldId(field.getId());
		return harvestProduct;
	}

	private AuctionUpdateDto createAuction(ProductDto product, TraderDetailDto trader, BigDecimal price, int quantity,
			LocalDateTime date, AuctionStrategyDto strategy, TradeStatusDto status) {
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
		store.setLocation("POINT(2.3522 48.8566)");
		store.setUserId(manager.getId());
		return store;
	}

	private AdminUpdateDto createAdmin(LanguageDto languageDto) {
		AdminUpdateDto admin = new AdminUpdateDto();
		admin.setFirstName("John");
		admin.setLastName("Doe");
		admin.setEmail("johndoe@gmail.com");
		admin.setPassword("azertyui");
		admin.setAddress("Rue de la Loi");
		admin.setEnabled(true);
		admin.setEnabled(true);
		admin.setRegistrationDate(LocalDateTime.now());
		admin.setValidationDate(LocalDateTime.now());
		admin.setPhone("+2290197000002");
		admin.setLanguageId(languageDto.getId());
		return admin;
	}

	private ProducerUpdateDto createProducer(LanguageDto languageDto) {
		ProducerUpdateDto producer = new ProducerUpdateDto();
		producer.setFirstName("Fabrice");
		producer.setLastName("Producer");
		producer.setEmail("fabricecipolla@gmail.com");
		producer.setPassword("azertyui");
		producer.setEnabled(true);
		producer.setAddress("Rue de la Paix");
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
		exporter.setAddress("Rue du Commerce");
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
		transformer.setAddress("Springfield");
		transformer.setEnabled(true);
		transformer.setRegistrationDate(LocalDateTime.now());
		transformer.setValidationDate(LocalDateTime.now());
		transformer.setPhone("+22944223201");
		transformer.setLanguageId(languageDto.getId());
		return transformer;
	}
}
