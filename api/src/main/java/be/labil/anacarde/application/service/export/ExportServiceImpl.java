package be.labil.anacarde.application.service.export;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.view.ExportAuctionDto;
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

	private final ExportAuctionRepository viewRepo;

	@Override
	public ExportAuctionDto getAuctionById(Integer auctionId) {
		return viewRepo.findByAuctionId(auctionId).orElseThrow(
				() -> new ResourceNotFoundException("aucune vue pour id=" + auctionId));
	}

	@Override
	public List<ExportAuctionDto> getAnalysesBetween(LocalDateTime start, LocalDateTime end,
			boolean onlyEnded) {
		return viewRepo.findAllByStartDateBetween(start, end, onlyEnded);
	}
}
