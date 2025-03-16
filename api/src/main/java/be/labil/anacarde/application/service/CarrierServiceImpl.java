package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.CarrierDto;
import be.labil.anacarde.domain.mapper.CarrierMapper;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.infrastructure.persistence.CarrierRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
/**
 * Ce service gère les opérations relatives aux transporteurs, y compris la création, récupération, mise à jour et
 * suppression. Il utilise un CarrierRepository pour la persistance et un CarrierMapper pour la conversion entre entités
 * et DTO.
 */
public class CarrierServiceImpl implements CarrierService {

	private final CarrierRepository carrierRepository;
	private final CarrierMapper carrierMapper;

	@Override
	public CarrierDto createCarrier(CarrierDto carrierDto) {
		Carrier carrier = carrierMapper.toEntity(carrierDto);
		Carrier saved = carrierRepository.save(carrier);
		return carrierMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public CarrierDto getCarrierById(Integer id) {
		Carrier carrier = carrierRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transporteur non trouvé"));
		return carrierMapper.toDto(carrier);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarrierDto> listCarriers() {
		return carrierRepository.findAll().stream().map(carrierMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public CarrierDto updateCarrier(Integer id, CarrierDto carrierDto) {
		Carrier existingCarrier = carrierRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transporteur non trouvé"));

		// Mets uniquement à jour les champs non nuls du DTO
		Carrier updatedCarrier = carrierMapper.partialUpdate(carrierDto, existingCarrier);

		Carrier saved = carrierRepository.save(updatedCarrier);
		return carrierMapper.toDto(saved);
	}

	@Override
	public void deleteCarrier(Integer id) {
		if (!carrierRepository.existsById(id)) {
			throw new ResourceNotFoundException("Transporteur non trouvé");
		}
		carrierRepository.deleteById(id);
	}
}
