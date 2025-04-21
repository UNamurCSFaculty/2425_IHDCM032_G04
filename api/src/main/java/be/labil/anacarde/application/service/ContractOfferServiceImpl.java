package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.ContractOfferDto;
import be.labil.anacarde.domain.mapper.ContractOfferMapper;
import be.labil.anacarde.domain.model.ContractOffer;
import be.labil.anacarde.infrastructure.persistence.ContractOfferRepository;
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

	@Override
	public ContractOfferDto createContractOffer(ContractOfferDto dto) {
		ContractOffer ContractOffer = contractOfferMapper.toEntity(dto);
		ContractOffer saved = contractOfferRepository.save(ContractOffer);
		return contractOfferMapper.toDto(saved);
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
	public ContractOfferDto updateContractOffer(Integer id, ContractOfferDto contractOfferDetailDto) {
		ContractOffer existingContractOffer = contractOfferRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contrat non trouvé"));

		ContractOffer updatedContractOffer = contractOfferMapper.partialUpdate(contractOfferDetailDto,
				existingContractOffer);

		ContractOffer saved = contractOfferRepository.save(updatedContractOffer);
		return contractOfferMapper.toDto(saved);
	}

	@Override
	public void deleteContractOffer(Integer id) {
		if (!contractOfferRepository.existsById(id)) {
			throw new ResourceNotFoundException("Contrat non trouvé");
		}
		contractOfferRepository.deleteById(id);
	}
}
