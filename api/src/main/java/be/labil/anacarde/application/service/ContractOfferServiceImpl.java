package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.ContractOfferDto;
import be.labil.anacarde.domain.dto.write.ContractOfferUpdateDto;
import be.labil.anacarde.domain.mapper.ContractOfferMapper;
import be.labil.anacarde.domain.model.ContractOffer;
import be.labil.anacarde.infrastructure.persistence.ContractOfferRepository;
import be.labil.anacarde.infrastructure.util.PersistenceHelper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ContractOfferServiceImpl implements ContractOfferService {
	private final ContractOfferRepository contractOfferRepository;
	private final ContractOfferMapper contractOfferMapper;
	private final PersistenceHelper persistenceHelper;

	@Override
	public ContractOfferDto createContractOffer(ContractOfferUpdateDto dto) {
		ContractOffer contractOffer = contractOfferMapper.toEntity(dto);
		ContractOffer full = persistenceHelper.saveAndReload(contractOfferRepository, contractOffer,
				ContractOffer::getId);
		return contractOfferMapper.toDto(full);
	}

	@Override
	@Transactional(readOnly = true)
	public ContractOfferDto getContractOfferById(Integer id) {
		ContractOffer ContractOffer = contractOfferRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contrat non trouvé"));
		return contractOfferMapper.toDto(ContractOffer);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ContractOfferDto> listContractOffers() {
		return contractOfferRepository.findAll().stream().map(contractOfferMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public ContractOfferDto updateContractOffer(Integer id, ContractOfferUpdateDto contractOfferDetailDto) {
		ContractOffer existingContractOffer = contractOfferRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contrat non trouvé"));

		ContractOffer updatedContractOffer = contractOfferMapper.partialUpdate(contractOfferDetailDto,
				existingContractOffer);

		ContractOffer full = persistenceHelper.saveAndReload(contractOfferRepository, updatedContractOffer,
				ContractOffer::getId);
		return contractOfferMapper.toDto(full);
	}

	@Override
	public void deleteContractOffer(Integer id) {
		if (!contractOfferRepository.existsById(id)) {
			throw new ResourceNotFoundException("Contrat non trouvé");
		}
		contractOfferRepository.deleteById(id);
	}
}
