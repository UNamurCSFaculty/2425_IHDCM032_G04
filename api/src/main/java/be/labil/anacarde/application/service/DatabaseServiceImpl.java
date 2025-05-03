package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.infrastructure.persistence.*;
import be.labil.anacarde.infrastructure.persistence.user.UserRepository;
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
	private final BidStatusRepository bidStatusRepository;
	private final FieldRepository fieldRepository;
	private final CooperativeRepository cooperativeRepository;
	private final RegionRepository regionRepository;
	private final DocumentRepository documentRepository;
	private final QualityRepository qualityRepository;
	private final ContractOfferRepository contractOfferRepository;
	private final QualityControlRepository qualityControlRepository;

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
		bidStatusRepository.deleteAllInBatch();
		auctionRepository.deleteAllInBatch();
		auctionStrategyRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		fieldRepository.deleteAllInBatch();
		storeRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		roleRepository.deleteAllInBatch();
		regionRepository.deleteAllInBatch();
		languageRepository.deleteAllInBatch();
	}
}
