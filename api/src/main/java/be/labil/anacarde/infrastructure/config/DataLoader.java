package be.labil.anacarde.infrastructure.config;

import be.labil.anacarde.application.service.*;
import be.labil.anacarde.domain.dto.*;
import be.labil.anacarde.domain.dto.user.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

	private final StoreService storeService;
	private final ProductService productService;
	private final UserService userService;
	private final AuctionService auctionService;
	private final TradeStatusService tradeStatusService;
	private final BidService bidService;
	private final DatabaseService databaseService;
	private final LanguageService languageService;
	private final CooperativeService cooperativeService;
	private final AuctionStrategyService auctionStrategyService;
	private final FieldService fieldService;

	@Override
	public void run(String... args) {
		databaseService.dropDatabase();

		// Langue
		LanguageDto languageFr = LanguageDto.builder().name("Français").code("fr").build();
		languageFr = languageService.createLanguage(languageFr);

		LanguageDto languageEn = LanguageDto.builder().name("English").code("en").build();
		languageEn = languageService.createLanguage(languageEn);

		// Création du producteur (sans coopérative) et d'un champ
		UserDetailDto producer = createProducer(languageFr);
		producer = userService.createUser(producer);
		FieldDto field = createField((ProducerDetailDto) producer);
		field = fieldService.createField(field);

		// Création de la coopérative
		CooperativeDto cooperativeDto = createCooperative((ProducerDetailDto) producer);
		cooperativeDto = cooperativeService.createCooperative(cooperativeDto);

		// Ajout du producteur à la coopérative
		((ProducerDetailDto) producer).setCooperative(cooperativeDto);
		producer.setPassword(null); // on ne veut pas que le mot de passe soit mis à jour
		producer = userService.updateUser(producer.getId(), producer);

		// Création de l'utilisateur admin
		UserDetailDto admin = createAdmin(languageFr);
		admin = userService.createUser(admin);

		// Création de l'utilisateur exportateur
		UserDetailDto exporter = createExporter(languageFr);
		exporter = userService.createUser(exporter);

		// Création de l'utilisateur transformateur
		UserDetailDto transformer = createTransformer(languageFr);
		transformer = userService.createUser(transformer);

		// Création d'un store
		StoreDetailDto store = createStore(admin);
		store = storeService.createStore(store);

		// Création d'un produit
		ProductDto product = createHarvestProduct(store, producer, field, 1000);
		product = productService.createProduct(product);

		ProductDto product2 = createHarvestProduct(store, producer, field, 2000);
		product2 = productService.createProduct(product2);

		// ProductDto product3 = createTransformedProduct("tombouctou", transformer, field)

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
		AuctionDto auction1 = createAuction(product, (TraderDetailDto) producer, BigDecimal.valueOf(500), 10,
				LocalDateTime.now(), auctionStrategy, tradeStatusOpen);
		AuctionDto auction2 = createAuction(product, (TraderDetailDto) producer, BigDecimal.valueOf(2500), 20,
				LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusOpen);
		AuctionDto auction3 = createAuction(product, (TraderDetailDto) producer, BigDecimal.valueOf(3500), 50,
				LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusOpen);
		AuctionDto auction4 = createAuction(product, (TraderDetailDto) producer, BigDecimal.valueOf(777), 50,
				LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusExpired);
		auction1 = auctionService.createAuction(auction1);
		auction2 = auctionService.createAuction(auction2);
		auction3 = auctionService.createAuction(auction3);
		auction4 = auctionService.createAuction(auction4);

		// Création d'enchères (pour l'utilisateur transformateur)
		AuctionDto auction5 = createAuction(product, (TraderDetailDto) transformer, BigDecimal.valueOf(999), 100,
				LocalDateTime.now().plusDays(5), auctionStrategy, tradeStatusOpen);
		auction5 = auctionService.createAuction(auction4);

		// Création d'offres
		BidDto bid1 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(100), LocalDateTime.now(),
				tradeStatusOpen);
		BidDto bid2 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(200), LocalDateTime.now(),
				tradeStatusOpen);
		BidDto bid3 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(300), LocalDateTime.now(),
				tradeStatusOpen);
		BidDto bid4 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(500), LocalDateTime.now(),
				tradeStatusOpen);
		BidDto bid5 = createBid(auction1, (TraderDetailDto) transformer, BigDecimal.valueOf(600), LocalDateTime.now(),
				tradeStatusOpen);
		BidDto bid6 = createBid(auction2, (TraderDetailDto) transformer, BigDecimal.valueOf(10), LocalDateTime.now(),
				tradeStatusOpen);
		BidDto bid7 = createBid(auction2, (TraderDetailDto) transformer, BigDecimal.valueOf(20), LocalDateTime.now(),
				tradeStatusOpen);
		bidService.createBid(bid1);
		bidService.createBid(bid2);
		bidService.createBid(bid3);
		bidService.createBid(bid4);
		bidService.createBid(bid5);
		bidService.createBid(bid6);
		bidService.createBid(bid7);

		System.out.println("🧪 Test data loaded (profile=dev)");
	}

	private CooperativeDto createCooperative(ProducerDetailDto producer) {
		CooperativeDto cooperativeDto = new CooperativeDto();
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

	private HarvestProductDto createHarvestProduct(StoreDetailDto store, UserDetailDto producer, FieldDto field,
			double weight) {
		HarvestProductDto harvestProduct = new HarvestProductDto();
		harvestProduct.setProducer((ProducerDetailDto) producer);
		harvestProduct.setStore(store);
		harvestProduct.setWeightKg(weight);
		harvestProduct.setDeliveryDate(LocalDateTime.now());
		harvestProduct.setField(field);
		return harvestProduct;
	}

	private AuctionDto createAuction(ProductDto product, TraderDetailDto trader, BigDecimal price, int quantity,
			LocalDateTime date, AuctionStrategyDto strategy, TradeStatusDto status) {
		AuctionDto auction = new AuctionDto();
		auction.setProduct(product);
		auction.setPrice(price);
		auction.setActive(true);
		auction.setCreationDate(date);
		auction.setExpirationDate(date.plusDays(20));
		auction.setProductQuantity(quantity);
		auction.setStrategy(strategy);
		auction.setTrader(trader);
		auction.setStatus(status);
		return auction;
	}

	private TradeStatusDto createTradeStatus(String name) {
		TradeStatusDto status = new TradeStatusDto();
		status.setName(name);
		return status;
	}

	private BidDto createBid(AuctionDto auction, TraderDetailDto trader, BigDecimal amount, LocalDateTime creationDate,
			TradeStatusDto status) {
		BidDto bid = new BidDto();
		bid.setAuction(auction);
		bid.setTrader(trader);
		bid.setAmount(amount);
		bid.setCreationDate(creationDate);
		bid.setStatus(status);
		return bid;
	}

	private StoreDetailDto createStore(UserDetailDto manager) {
		StoreDetailDto store = new StoreDetailDto();
		store.setName("Nassara");
		store.setLocation("POINT(2.3522 48.8566)");
		store.setUserId(manager.getId());
		return store;
	}

	private AdminDetailDto createAdmin(LanguageDto languageDto) {
		AdminDetailDto admin = new AdminDetailDto();
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
		admin.setLanguage(languageDto);
		return admin;
	}

	private ProducerDetailDto createProducer(LanguageDto languageDto) {
		ProducerDetailDto producer = new ProducerDetailDto();
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
		producer.setLanguage(languageDto);
		return producer;
	}

	private ExporterDetailDto createExporter(LanguageDto languageDto) {
		ExporterDetailDto exporter = new ExporterDetailDto();
		exporter.setFirstName("Stéphane");
		exporter.setLastName("Exporter");
		exporter.setEmail("stephaneglibert@gmail.com");
		exporter.setPassword("azertyui");
		exporter.setAddress("Rue du Commerce");
		exporter.setEnabled(true);
		exporter.setRegistrationDate(LocalDateTime.now());
		exporter.setValidationDate(LocalDateTime.now());
		exporter.setPhone("+2290197000001");
		exporter.setLanguage(languageDto);
		return exporter;
	}

	private TransformerDetailDto createTransformer(LanguageDto languageDto) {
		TransformerDetailDto transformer = new TransformerDetailDto();
		transformer.setFirstName("Homer");
		transformer.setLastName("Transformer");
		transformer.setEmail("homer@gmail.com");
		transformer.setPassword("azertyui");
		transformer.setAddress("Springfield");
		transformer.setEnabled(true);
		transformer.setRegistrationDate(LocalDateTime.now());
		transformer.setValidationDate(LocalDateTime.now());
		transformer.setPhone("+22944223201");
		transformer.setLanguage(languageDto);
		return transformer;
	}
}
