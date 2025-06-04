package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.StoreDetailDto;
import be.labil.anacarde.domain.dto.write.StoreUpdateDto;
import be.labil.anacarde.domain.mapper.StoreMapper;
import be.labil.anacarde.domain.model.City;
import be.labil.anacarde.domain.model.Region;
import be.labil.anacarde.domain.model.Store;
import be.labil.anacarde.infrastructure.persistence.StoreRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class StoreServiceImpl implements StoreService {
	private final StoreRepository storeRepository;
	private final StoreMapper storeMapper;
	private final GeoService geoService;

	@Override
	public StoreDetailDto createStore(StoreUpdateDto dto) {
		Store store = storeMapper.toEntity(dto);

		City city = geoService.findCityById(dto.getAddress().getCityId());
		Region region = geoService.findRegionByCityId(city);
		store.getAddress().setCity(city);
		store.getAddress().setRegion(region);

		Store saved = storeRepository.save(store);
		return storeMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public StoreDetailDto getStoreById(Integer id) {
		Store Store = storeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Magasin non trouvé"));
		return storeMapper.toDto(Store);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StoreDetailDto> listStores() {
		return storeRepository.findAll().stream().map(storeMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public StoreDetailDto updateStore(Integer id, StoreUpdateDto dto) {
		Store existingStore = storeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Magasin non trouvé"));
		// Mets uniquement à jour les champs non nuls du DTO
		Store updatedStore = storeMapper.partialUpdate(dto, existingStore);

		if (dto.getAddress() != null) {
			City city = geoService.findCityById(dto.getAddress().getCityId());
			Region region = geoService.findRegionByCityId(city);
			existingStore.getAddress().setCity(city);
			existingStore.getAddress().setRegion(region);
		}

		Store saved = storeRepository.save(updatedStore);
		return storeMapper.toDto(saved);
	}

	@Override
	public void deleteStore(Integer id) {
		if (!storeRepository.existsById(id)) {
			throw new ResourceNotFoundException("Magasin non trouvé");
		}
		storeRepository.deleteById(id);
	}

	@Override
	public boolean existsStoreByUserId(Integer userId) {
		return storeRepository.existsStoreByUserId(userId);
	}
}
