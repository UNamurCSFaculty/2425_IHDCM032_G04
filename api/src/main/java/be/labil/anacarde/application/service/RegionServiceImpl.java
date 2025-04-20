package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.RegionDto;
import be.labil.anacarde.domain.mapper.RegionMapper;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Region;
import be.labil.anacarde.infrastructure.persistence.RegionRepository;
import be.labil.anacarde.infrastructure.persistence.user.CarrierRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class RegionServiceImpl implements RegionService {

	private final RegionRepository regionRepository;
	private final CarrierRepository carrierRepository;
	private final RegionMapper regionMapper;

	@Override
	public RegionDto createRegion(RegionDto dto) {
		Region region = regionMapper.toEntity(dto);
		Region saved = regionRepository.save(region);
		return regionMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public RegionDto getRegion(Integer id) {
		Region region = regionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Région non trouvée"));
		return regionMapper.toDto(region);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RegionDto> listRegions(Integer carrierId) {
		List<Region> regions;
		if (carrierId == null) {
			regions = regionRepository.findAll();
		} else {
			regions = carrierRepository.findCarrierRegions(carrierId);
		}
		return regions.stream().map(regionMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public RegionDto updateRegion(Integer id, RegionDto dto) {
		Region existing = regionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Région non trouvée"));

		Region updated = regionMapper.partialUpdate(dto, existing);
		Region saved = regionRepository.save(updated);
		return regionMapper.toDto(saved);
	}

	@Override
	public void deleteRegion(Integer id) {
		if (!regionRepository.existsById(id)) {
			throw new ResourceNotFoundException("Région non trouvée");
		}
		regionRepository.deleteById(id);
	}

	@Override
	public void addCarrier(Integer carrierId, Integer regionId) {
		Carrier carrier = carrierRepository.findById(carrierId)
				.orElseThrow(() -> new ResourceNotFoundException("Transporter non Trouvé"));
		Region region = regionRepository.findById(regionId)
				.orElseThrow(() -> new ResourceNotFoundException("Région non trouvée"));

		carrier.getRegions().add(region);
		carrierRepository.save(carrier);
	}
}
