package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.BidService;
import be.labil.anacarde.domain.dto.db.BidDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class BidApiController implements BidApi {
	private final BidService bidService;

	@Override
	public ResponseEntity<BidDto> getBid(Integer bidId) {
		BidDto bid = bidService.getBidById(bidId);
		return ResponseEntity.ok(bid);
	}

	@Override
	public ResponseEntity<List<BidDto>> listBids(Integer auctionId) {
		List<BidDto> bids = bidService.listBids(auctionId);
		return ResponseEntity.ok(bids);
	}

	@Override
	public ResponseEntity<BidDto> createBid(BidUpdateDto bidDto) {
		BidDto created = bidService.createBid(bidDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<BidDto> updateBid(Integer bidId, BidUpdateDto bidDto) {
		BidDto updated = bidService.updateBid(bidId, bidDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<BidDto> acceptBid(Integer bidId) {
		BidDto updated = bidService.acceptBid(bidId);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<BidDto> rejectBid(Integer bidId) {
		BidDto updated = bidService.rejectBid(bidId);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteBid(Integer id) {
		bidService.deleteBid(id);
		return ResponseEntity.noContent().build();
	}
}
