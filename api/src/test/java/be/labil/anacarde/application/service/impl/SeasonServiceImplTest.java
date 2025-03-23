package be.labil.anacarde.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.application.service.SeasonServiceImpl;
import be.labil.anacarde.domain.dto.SeasonDto;
import be.labil.anacarde.domain.mapper.SeasonMapper;
import be.labil.anacarde.domain.model.Season;
import be.labil.anacarde.infrastructure.persistence.SeasonRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SeasonServiceImplTest {

	@Mock
	private SeasonRepository seasonRepository;

	@Mock
	private SeasonMapper seasonMapper;

	@InjectMocks
	private SeasonServiceImpl seasonService;

	private Season season;
	private SeasonDto seasonDto;

	@BeforeEach
	void setUp() {
		season = new Season();
		season.setId(1);
		season.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0));
		season.setEndDate(LocalDateTime.of(2024, 12, 31, 23, 59));

		seasonDto = new SeasonDto();
		seasonDto.setId(1);
		seasonDto.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0));
		seasonDto.setEndDate(LocalDateTime.of(2024, 12, 31, 23, 59));

		Mockito.lenient().when(seasonMapper.toEntity(any(SeasonDto.class))).thenReturn(season);
		Mockito.lenient().when(seasonMapper.toDto(any(Season.class))).thenReturn(seasonDto);
	}

	@Test
	void testCreateSeason() {
		when(seasonRepository.save(season)).thenReturn(season);
		SeasonDto result = seasonService.createSeason(seasonDto);
		assertThat(result).isEqualTo(seasonDto);
		verify(seasonRepository, times(1)).save(season);
	}

	@Test
	void testGetSeasonByIdSuccess() {
		when(seasonRepository.findById(1)).thenReturn(Optional.of(season));
		SeasonDto result = seasonService.getSeasonById(1);
		assertThat(result).isEqualTo(seasonDto);
	}

	@Test
	void testGetSeasonByIdNotFound() {
		when(seasonRepository.findById(1)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> seasonService.getSeasonById(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Saison non trouvée");
	}

	@Test
	void testListSeasons() {
		when(seasonRepository.findAll()).thenReturn(Collections.singletonList(season));
		List<SeasonDto> result = seasonService.listSeasons();
		assertThat(result).hasSize(1).contains(seasonDto);
	}

	@Test
	void testUpdateSeason() {
		SeasonDto updatedDto = new SeasonDto();
		updatedDto.setStartDate(LocalDateTime.of(2024, 1, 1, 0, 0));
		updatedDto.setEndDate(LocalDateTime.of(2024, 12, 31, 23, 59));

		when(seasonRepository.findById(1)).thenReturn(Optional.of(season));
		when(seasonMapper.partialUpdate(any(SeasonDto.class), any(Season.class))).thenAnswer(invocation -> {
			SeasonDto dto = invocation.getArgument(0);
			Season existing = invocation.getArgument(1);
			if (dto.getStartDate() != null) {
				existing.setStartDate(dto.getStartDate());
			}
			if (dto.getEndDate() != null) {
				existing.setEndDate(dto.getEndDate());
			}
			return existing;
		});
		when(seasonRepository.save(season)).thenReturn(season);
		when(seasonMapper.toDto(season)).thenReturn(updatedDto);

		SeasonDto result = seasonService.updateSeason(1, updatedDto);
		assertThat(result.getStartDate()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
		assertThat(result.getEndDate()).isEqualTo(LocalDateTime.of(2024, 12, 31, 23, 59));
	}

	@Test
	void testDeleteSeasonSuccess() {
		when(seasonRepository.existsById(1)).thenReturn(true);
		seasonService.deleteSeason(1);
		verify(seasonRepository, times(1)).deleteById(1);
	}

	@Test
	void testDeleteSeasonNotFound() {
		when(seasonRepository.existsById(1)).thenReturn(false);
		assertThatThrownBy(() -> seasonService.deleteSeason(1)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Saison non trouvée");
	}
}
