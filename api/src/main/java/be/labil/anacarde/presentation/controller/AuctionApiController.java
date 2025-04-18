package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.AuctionService;
import be.labil.anacarde.domain.dto.AuctionDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class AuctionApiController implements AuctionApi {
	private final AuctionService auctionService;

	@Override
	public ResponseEntity<? extends AuctionDto> getAuction(Integer id) {
		AuctionDto auction = auctionService.getAuctionById(id);
		return ResponseEntity.ok(auction);
	}

	@Override
	public ResponseEntity<List<? extends AuctionDto>> listAuctions() {
		List<AuctionDto> auctions = auctionService.listAuctions();
		return ResponseEntity.ok(auctions);
	}

	@Override
	public ResponseEntity<AuctionDto> createAuction(AuctionDto auctionDetailDto) {
		AuctionDto created = auctionService.createAuction(auctionDetailDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<AuctionDto> updateAuction(Integer id, AuctionDto auctionDetailDto) {
		AuctionDto updated = auctionService.updateAuction(id, auctionDetailDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteAuction(Integer id) {
		auctionService.deleteAuction(id);
		return ResponseEntity.noContent().build();
	}
}
