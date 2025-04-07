package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.ExporterDetailDto;
import be.labil.anacarde.domain.model.Exporter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExporterDetailMapper extends GenericMapper<ExporterDetailDto, Exporter> {

	@Override
	Exporter toEntity(ExporterDetailDto dto);

	@Override
	ExporterDetailDto toDto(Exporter entity);
}
