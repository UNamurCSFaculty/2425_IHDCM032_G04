package be.labil.anacarde.application.service.export;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.view.DashboardCardsDto;
import be.labil.anacarde.domain.dto.db.view.DashboardGraphicDto;
import be.labil.anacarde.domain.mapper.DashboardCardsMapper;
import be.labil.anacarde.domain.mapper.DashboardGraphicMapper;
import be.labil.anacarde.infrastructure.persistence.view.DashboardCardsRepository;
import be.labil.anacarde.infrastructure.persistence.view.DashboardGraphicRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation <strong>lecture-seule</strong> des services de tableau de bord.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

	/* ===== Repositories ===== */
	private final DashboardCardsRepository cardsRepo;
	private final DashboardGraphicRepository graphicRepo;

	/* ===== Mappers ===== */
	private final DashboardCardsMapper cardsMapper;
	private final DashboardGraphicMapper graphicMapper;

	/* ===== KPI « cards » ===== */
	@Override
	public DashboardCardsDto getDashboardStats() {
		return cardsRepo.fetchCards() // 0 ou 1 ligne
				.map(cardsMapper::toDto).orElseThrow(() -> new ResourceNotFoundException(
						"La vue v_dashboard_cards n’a retourné aucune ligne."));
	}

	/* ===== Série chronologique ===== */
	@Override
	public List<DashboardGraphicDto> getDashboardGraphicSeries() {
		return graphicMapper.toDtoList(graphicRepo.findAllSeries());
	}
}
