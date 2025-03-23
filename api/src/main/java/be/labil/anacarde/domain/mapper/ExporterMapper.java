package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.ExporterDto;
import be.labil.anacarde.domain.model.Exporter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre Exporter et ExporterDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExporterMapper extends GenericMapper<ExporterDto, Exporter> {

	@Override
	@Mapping(source = "bidder.id", target = "bidderId")
	ExporterDto toDto(Exporter exporter);

	@Override
	@Mapping(source = "bidderId", target = "bidder.id")
	Exporter toEntity(ExporterDto exporterDto);
}