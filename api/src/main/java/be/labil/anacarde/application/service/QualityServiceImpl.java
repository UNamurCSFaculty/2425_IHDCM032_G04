package be.labil.anacarde.application.service;

import be.labil.anacarde.application.exception.ResourceNotFoundException;
import be.labil.anacarde.domain.dto.db.QualityDto;
import be.labil.anacarde.domain.mapper.QualityMapper;
import be.labil.anacarde.domain.model.Quality;
import be.labil.anacarde.infrastructure.persistence.QualityRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class QualityServiceImpl implements QualityService {
	private final QualityRepository qualityRepository;
	private final QualityMapper qualityMapper;

	@Override
	public QualityDto createQuality(QualityDto dto) {
		Quality quality = qualityMapper.toEntity(dto);
		Quality saved = qualityRepository.save(quality);
		return qualityMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public QualityDto getQualityById(Integer id) {
		Quality Quality = qualityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Qualité non trouvée"));
		return qualityMapper.toDto(Quality);
	}

	@Override
	@Transactional(readOnly = true)
	public List<QualityDto> listQualities() {
		return qualityRepository.findAll().stream().map(qualityMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public QualityDto updateQuality(Integer id, QualityDto qualityDetailDto) {
		Quality existingQuality = qualityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Qualité non trouvée"));

		Quality updatedQuality = qualityMapper.partialUpdate(qualityDetailDto, existingQuality);

		Quality saved = qualityRepository.save(updatedQuality);
		return qualityMapper.toDto(saved);
	}

	@Override
	public void deleteQuality(Integer id) {
		if (!qualityRepository.existsById(id)) {
			throw new ResourceNotFoundException("Qualité non trouvée");
		}
		qualityRepository.deleteById(id);
	}
}
