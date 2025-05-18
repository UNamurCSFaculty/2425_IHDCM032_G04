package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.user.ExporterDetailDto;
import be.labil.anacarde.domain.model.Exporter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, RoleMapper.class,
		LanguageMapper.class, AddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ExporterDetailMapper {

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	@Mapping(source = "address", target = "address")
	public abstract Exporter toEntity(ExporterDetailDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	@Mapping(source = "address", target = "address")
	public abstract ExporterDetailDto toDto(Exporter entity);
}
