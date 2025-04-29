package be.labil.anacarde.application.service;

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

	@Override
	public void dropDatabase() {
		contractOfferRepository.deleteAll();
		qualityRepository.deleteAll();
		documentRepository.deleteAll();
		cooperativeRepository.deleteAll();
		fieldRepository.deleteAll();
		bidRepository.deleteAll();
		bidStatusRepository.deleteAll();
		auctionRepository.deleteAll();
		auctionStrategyRepository.deleteAll();
		productRepository.deleteAll();
		storeRepository.deleteAll();
		userRepository.deleteAll();
		roleRepository.deleteAll();
		regionRepository.deleteAll();
		languageRepository.deleteAll();
	}
}
