package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.ExporterDetailDto;
import be.labil.anacarde.domain.model.Exporter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExporterDetailMapper extends GenericMapper<ExporterDetailDto, Exporter> {

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	Exporter toEntity(ExporterDetailDto dto);

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	ExporterDetailDto toDto(Exporter entity);
}
