package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.AuctionStrategyService;
import be.labil.anacarde.domain.dto.AuctionStrategyDto;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class AuctionStrategyApiController implements AuctionStrategyApi {

	private final AuctionStrategyService auctionStrategyService;

	@Override
	public ResponseEntity<? extends AuctionStrategyDto> getAuctionStrategy(Integer id) {
		AuctionStrategyDto strategy = auctionStrategyService.getAuctionStrategyById(id);
		return ResponseEntity.ok(strategy);
	}

	@Override
	public ResponseEntity<List<? extends AuctionStrategyDto>> listAuctionStrategies() {
		List<AuctionStrategyDto> strategies = auctionStrategyService.listAuctionStrategies();
		return ResponseEntity.ok(strategies);
	}

	@Override
	public ResponseEntity<? extends AuctionStrategyDto> createAuctionStrategy(AuctionStrategyDto auctionStrategyDto) {
		AuctionStrategyDto created = auctionStrategyService.createAuctionStrategy(auctionStrategyDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<? extends AuctionStrategyDto> updateAuctionStrategy(Integer id,
			AuctionStrategyDto auctionStrategyDto) {
		AuctionStrategyDto updated = auctionStrategyService.updateAuctionStrategy(id, auctionStrategyDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteAuctionStrategy(Integer id) {
		auctionStrategyService.deleteAuctionStrategy(id);
		return ResponseEntity.noContent().build();
	}
}
