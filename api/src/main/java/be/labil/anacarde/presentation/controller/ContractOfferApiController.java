package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.ContractOfferService;
import be.labil.anacarde.domain.dto.db.ContractOfferDto;
import be.labil.anacarde.domain.dto.write.ContractOfferUpdateDto;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class ContractOfferApiController implements ContractOfferApi {
	private final ContractOfferService contractOfferService;

	@Override
	public ResponseEntity<ContractOfferDto> getContractOffer(Integer id) {
		ContractOfferDto contractOffer = contractOfferService.getContractOfferById(id);
		return ResponseEntity.ok(contractOffer);
	}

	@Override
	public ResponseEntity<List<ContractOfferDto>> listContractOffers(Integer traderId,
			Integer qualityId, Integer sellerId, Integer buyerId) {
		List<ContractOfferDto> contractOffers = new ArrayList<>();

		if (qualityId != null && sellerId != null && buyerId != null) {
			try {
				ContractOfferDto offer = contractOfferService.getContractOfferByCriteria(qualityId,
						sellerId, buyerId);
				contractOffers.add(offer);
			}
			catch (ResourceNotFoundException e) {
				// no contract, return empty list
			}
		} else {
			contractOffers = contractOfferService.listContractOffers(traderId);
		}
		return ResponseEntity.ok(contractOffers);
	}

	@Override
	public ResponseEntity<ContractOfferDto> createContractOffer(
			ContractOfferUpdateDto contractOfferDetailDto) {
		ContractOfferDto created = contractOfferService.createContractOffer(contractOfferDetailDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<ContractOfferDto> updateContractOffer(Integer id,
			ContractOfferUpdateDto contractOfferDetailDto) {
		ContractOfferDto updated = contractOfferService.updateContractOffer(id,
				contractOfferDetailDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<ContractOfferDto> acceptContractOffer(Integer contractOfferId) {
		ContractOfferDto updated = contractOfferService.acceptContractOffer(contractOfferId);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<ContractOfferDto> rejectContractOffer(Integer contractOfferId) {
		ContractOfferDto updated = contractOfferService.rejectContractOffer(contractOfferId);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteContractOffer(Integer id) {
		contractOfferService.deleteContractOffer(id);
		return ResponseEntity.noContent().build();
	}
}
