package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.AuctionService;
import be.labil.anacarde.application.service.GlobalSettingsService;
import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
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
	private final GlobalSettingsService globalSettingsService;

	@Override
	public ResponseEntity<AuctionDto> getAuction(Integer id) {
		AuctionDto auction = auctionService.getAuctionById(id);
		return ResponseEntity.ok(auction);
	}

	@Override
	public ResponseEntity<GlobalSettingsDto> getAuctionSettings() {
		GlobalSettingsDto settingsDto = globalSettingsService.getGlobalSettings();
		return ResponseEntity.ok(settingsDto);
	}

	@Override
	public ResponseEntity<List<AuctionDto>> listAuctions(Integer traderId, Integer buyerId,
			String auctionStatus) {
		List<AuctionDto> auctions = auctionService.listAuctions(traderId, buyerId, auctionStatus);
		return ResponseEntity.ok(auctions);
	}

	@Override
	public ResponseEntity<AuctionDto> createAuction(AuctionUpdateDto auctionUpdateDto) {
		AuctionDto created = auctionService.createAuction(auctionUpdateDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@Override
	public ResponseEntity<AuctionDto> updateAuction(Integer id, AuctionUpdateDto auctionUpdateDto) {
		AuctionDto updated = auctionService.updateAuction(id, auctionUpdateDto);
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
