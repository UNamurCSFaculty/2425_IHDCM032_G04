package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.AuctionService;
import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
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
	public ResponseEntity<AuctionDto> getAuction(Integer id) {
		AuctionDto auction = auctionService.getAuctionById(id);
		return ResponseEntity.ok(auction);
	}

	@Override
	public ResponseEntity<List<AuctionDto>> listAuctions(Integer traderId, String auctionStatus) {
		List<AuctionDto> auctions = auctionService.listAuctions(traderId, auctionStatus);
		return ResponseEntity.ok(auctions);
	}

	@Override
	public ResponseEntity<AuctionDto> createAuction(AuctionUpdateDto auctionDetailDto) {
		AuctionDto created = auctionService.createAuction(auctionDetailDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<AuctionDto> updateAuction(Integer id, AuctionUpdateDto auctionDetailDto) {
		AuctionDto updated = auctionService.updateAuction(id, auctionDetailDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<AuctionDto> acceptAuction(Integer id) {
		AuctionDto updated = auctionService.acceptAuction(id);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteAuction(Integer id) {
		auctionService.deleteAuction(id);
		return ResponseEntity.noContent().build();
	}
}
