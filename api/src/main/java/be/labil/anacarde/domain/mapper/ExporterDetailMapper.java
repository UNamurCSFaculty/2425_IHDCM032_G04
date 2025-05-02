package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.ExporterDetailDto;
import be.labil.anacarde.domain.model.Exporter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ExporterDetailMapper {

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract Exporter toEntity(ExporterDetailDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract ExporterDetailDto toDto(Exporter entity);
}
