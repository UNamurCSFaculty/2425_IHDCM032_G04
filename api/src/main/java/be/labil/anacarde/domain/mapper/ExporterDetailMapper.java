package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.user.ExporterDetailDto;
import be.labil.anacarde.domain.model.Exporter;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, LanguageMapper.class,
		AddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ExporterDetailMapper {

	public abstract Exporter toEntity(ExporterDetailDto dto);

	public abstract ExporterDetailDto toDto(Exporter entity);
}
