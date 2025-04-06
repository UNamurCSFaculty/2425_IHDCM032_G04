package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.SeasonDto;
import be.labil.anacarde.domain.mapper.SeasonMapper;
import be.labil.anacarde.domain.model.Season;
import be.labil.anacarde.infrastructure.persistence.SeasonRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class SeasonServiceImpl implements SeasonService {

	private final SeasonRepository seasonRepository;
	private final SeasonMapper seasonMapper;

	@Override
	public SeasonDto createSeason(SeasonDto seasonDto) {
		Season season = seasonMapper.toEntity(seasonDto);
		Season saved = seasonRepository.save(season);
		return seasonMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public SeasonDto getSeasonById(Integer id) {
		Season season = seasonRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée"));
		return seasonMapper.toDto(season);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SeasonDto> listSeasons() {
		return seasonRepository.findAll().stream().map(seasonMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public SeasonDto updateSeason(Integer id, SeasonDto seasonDto) {
		Season existingSeason = seasonRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée"));

		Season updatedSeason = seasonMapper.partialUpdate(seasonDto, existingSeason);
		Season saved = seasonRepository.save(updatedSeason);
		return seasonMapper.toDto(saved);
	}

	@Override
	public void deleteSeason(Integer id) {
		if (!seasonRepository.existsById(id)) {
			throw new ResourceNotFoundException("Saison non trouvée");
		}
		seasonRepository.deleteById(id);
	}
}
