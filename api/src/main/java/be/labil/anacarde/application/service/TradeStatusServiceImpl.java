package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.TradeStatusDto;
import be.labil.anacarde.domain.mapper.TradeStatusMapper;
import be.labil.anacarde.domain.model.TradeStatus;
import be.labil.anacarde.infrastructure.persistence.TradeStatusRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TradeStatusServiceImpl implements TradeStatusService {
	private final TradeStatusRepository TradeStatusRepository;
	private final TradeStatusMapper TradeStatusMapper;

	@Override
	public TradeStatusDto createTradeStatus(TradeStatusDto dto) {
		TradeStatus tradeStatus = TradeStatusMapper.toEntity(dto);
		TradeStatus saved = TradeStatusRepository.save(tradeStatus);
		return TradeStatusMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public TradeStatusDto getTradeStatusById(Integer id) {
		TradeStatus TradeStatus = TradeStatusRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Enchère non trouvée"));
		return TradeStatusMapper.toDto(TradeStatus);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TradeStatusDto> listTradeStatus() {
		List<TradeStatus> tradeStatuses = TradeStatusRepository.findAll();
		return tradeStatuses.stream().map(TradeStatusMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public TradeStatusDto updateTradeStatus(Integer id, TradeStatusDto TradeStatusDetailDto) {
		TradeStatus existingTradeStatus = TradeStatusRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Status non trouvé"));

		TradeStatus updatedTradeStatus = TradeStatusMapper.partialUpdate(TradeStatusDetailDto,
				existingTradeStatus);

		TradeStatus saved = TradeStatusRepository.save(updatedTradeStatus);
		return TradeStatusMapper.toDto(saved);
	}

	@Override
	public void deleteTradeStatus(Integer id) {
		if (!TradeStatusRepository.existsById(id)) {
			throw new ResourceNotFoundException("Status non trouvé");
		}
		TradeStatusRepository.deleteById(id);
	}
}
