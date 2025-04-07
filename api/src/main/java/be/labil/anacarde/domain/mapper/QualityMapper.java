package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.QualityDto;
import be.labil.anacarde.domain.model.Quality;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QualityMapper extends GenericMapper<QualityDto, Quality> {

	@Override
	QualityDto toDto(Quality entity);

	@Override
	Quality toEntity(QualityDto dto);
}
