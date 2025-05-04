package be.labil.anacarde.infrastructure.config;

import be.labil.anacarde.application.service.*;
import be.labil.anacarde.domain.dto.*;
import be.labil.anacarde.domain.dto.user.AdminDetailDto;
import be.labil.anacarde.domain.dto.user.ExporterDetailDto;
import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.user.UserDetailDto;
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
	private final DatabaseService databaseService;
	private final LanguageService languageService;
	private final CooperativeService cooperativeService;
	private final AuctionStrategyService auctionStrategyService;
	private final FieldService fieldService;
	// Repositories

	@Override
	public void run(String... args) {

		if (!userService.listUsers().isEmpty()) {
			log.info("Database already initialized");
			return;
		}
		databaseService.dropDatabase();

		// 1. Langue
		LanguageDto languageFr = LanguageDto.builder().name("Français").code("fr").build();
		languageFr = languageService.createLanguage(languageFr);
		LanguageDto languageEn = LanguageDto.builder().name("English").code("en").build();
		languageEn = languageService.createLanguage(languageEn);

		// 2. Création du producteur (sans coopérative) et d'un champ
		UserDetailDto producer = createProducer(languageFr);
		producer = userService.createUser(producer);
		FieldDto field = createField((ProducerDetailDto) producer);
		field = fieldService.createField(field);

		// 3. Création de la coopérative
		CooperativeDto cooperativeDto = createCooperative((ProducerDetailDto) producer);
		cooperativeDto = cooperativeService.createCooperative(cooperativeDto);

		// 4. Ajout du producteur à la coopérative
		((ProducerDetailDto) producer).setCooperative(cooperativeDto);
		producer.setPassword(null); // on ne veut pas que le mot de passe soit mis à jour
		producer = userService.updateUser(producer.getId(), producer);

		// 5. Création de l'utilisateur admin
		UserDetailDto admin = createAdmin(languageFr);
		admin = userService.createUser(admin);

		// 6. Création de l'utilisateur exportateur
		UserDetailDto exporter = createExporter(languageFr);
		exporter = userService.createUser(exporter);

		// 7. Création d'un store
		StoreDetailDto store = createStore(admin);
		store = storeService.createStore(store);

		// 8. Création d'un produit
		ProductDto product = createHarvestProduct(store, producer, field, 1000);
		product = productService.createProduct(product);

		// 9. Création d'une stratégie d'enchère
		AuctionStrategyDto auctionStrategy = new AuctionStrategyDto();
		auctionStrategy.setName("Stratégie de test");
		auctionStrategy = auctionStrategyService.createAuctionStrategy(auctionStrategy);

		// 10. Création d'une enchère
		AuctionDto auction1 = createAuction(product, BigDecimal.valueOf(500), 10, LocalDateTime.now(), auctionStrategy);
		AuctionDto auction2 = createAuction(product, BigDecimal.valueOf(2500), 10, LocalDateTime.now().plusDays(5),
				auctionStrategy);
		auction1 = auctionService.createAuction(auction1);
		auction2 = auctionService.createAuction(auction2);

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

	private AuctionDto createAuction(ProductDto product, BigDecimal price, int quantity, LocalDateTime date,
			AuctionStrategyDto strategy) {
		AuctionDto auction = new AuctionDto();
		auction.setProduct(product);
		auction.setPrice(price);
		auction.setActive(true);
		auction.setCreationDate(date);
		auction.setExpirationDate(date.plusDays(20));
		auction.setProductQuantity(quantity);
		auction.setStrategy(strategy);
		return auction;
	}

	private StoreDetailDto createStore(UserDetailDto manager) {
		StoreDetailDto store = new StoreDetailDto();
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
		producer.setLastName("Cipolla");
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
		exporter.setFirstName("Stephane");
		exporter.setLastName("Glibert");
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
}
