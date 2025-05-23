package be.labil.anacarde.presentation.controller;

import be.labil.anacarde.application.service.RegionService;
import be.labil.anacarde.domain.dto.db.RegionDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegionApiController implements RegionApi {

	private final RegionService regionService;

	@Override
	public ResponseEntity<RegionDto> getRegion(Integer id) {
		RegionDto region = regionService.getRegion(id);
		return ResponseEntity.ok(region);
	}

	@Override
	public ResponseEntity<List<RegionDto>> listRegions(Integer carrierId) {
		List<RegionDto> regions = regionService.listRegions(carrierId);
		return ResponseEntity.ok(regions);
	}

	/*
	 * @Override public ResponseEntity<RegionDto> createRegion(RegionDto regionDto) { RegionDto
	 * created = regionService.createRegion(regionDto); URI location =
	 * ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
	 * .buildAndExpand(created.getId()).toUri(); return
	 * ResponseEntity.created(location).body(created); }
	 */

	@Override
	public ResponseEntity<RegionDto> updateRegion(Integer id, RegionDto regionDto) {
		RegionDto updated = regionService.updateRegion(id, regionDto);
		return ResponseEntity.ok(updated);
	}

	@Override
	public ResponseEntity<Void> deleteRegion(Integer id) {
		regionService.deleteRegion(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> addCarrier(Integer carrierId, Integer regionId) {
		regionService.addCarrier(carrierId, regionId);
		return ResponseEntity.ok().build();
	}
}
