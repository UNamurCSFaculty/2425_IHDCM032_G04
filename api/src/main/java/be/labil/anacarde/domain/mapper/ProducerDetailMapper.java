package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProducerDetailMapper extends GenericMapper<ProducerDetailDto, Producer> {

	@Override
	@Mapping(source = "cooperative", target = "cooperative")
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	Producer toEntity(ProducerDetailDto dto);

	@Override
	@Mapping(source = "cooperative", target = "cooperative")
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	ProducerDetailDto toDto(Producer entity);
}
