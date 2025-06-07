package be.labil.anacarde.application.service.export;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
import be.labil.anacarde.domain.mapper.ExportAuctionMapper;
import be.labil.anacarde.infrastructure.persistence.view.ExportAuctionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportServiceImpl implements ExportService {

	private final ExportAuctionRepository repo;
	private final ExportAuctionMapper mapper;

	@Override
	public List<ExportAuctionDto> getAllAnalyses() {
		return repo.findAllView() // ← la nouvelle requête native
				.stream().map(mapper::toDto).toList();
	}

	@Override
	public ExportAuctionDto getAuctionById(Integer auctionId) {
		return repo.findByAuctionId(auctionId).map(mapper::toDto).orElseThrow(
				() -> new ResourceNotFoundException("aucune vue pour id=" + auctionId));
	}

	@Override
	public List<ExportAuctionDto> getAnalysesBetween(LocalDateTime start, LocalDateTime end,
			boolean onlyEnded) {
		return repo.findAllByStartDateBetween(start, end, onlyEnded).stream().map(mapper::toDto)
				.toList();
	}
}
