package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.user.ProducerDetailDto;
import be.labil.anacarde.domain.model.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProducerDetailMapper {

	@Mapping(source = "cooperative", target = "cooperative")
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract Producer toEntity(ProducerDetailDto dto);

	@Mapping(source = "cooperative", target = "cooperative")
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract ProducerDetailDto toDto(Producer entity);
}
