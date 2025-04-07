package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.TransformerDetailDto;
import be.labil.anacarde.domain.model.Transformer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransformerDetailMapper extends GenericMapper<TransformerDetailDto, Transformer> {

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	Transformer toEntity(TransformerDetailDto dto);

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	TransformerDetailDto toDto(Transformer entity);
}
