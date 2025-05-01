package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.BidService;
import be.labil.anacarde.domain.dto.BidDto;
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
	public ResponseEntity<BidDto> createBid(BidDto bidDto) {
		BidDto created = bidService.createBid(bidDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<BidDto> updateBid(Integer id, BidDto bidDto) {
		BidDto updated = bidService.updateBid(id, bidDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteBid(Integer id) {
		bidService.deleteBid(id);
		return ResponseEntity.noContent().build();
	}
}
