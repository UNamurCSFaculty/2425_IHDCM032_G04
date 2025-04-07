package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.QualityInspectorDetailDto;
import be.labil.anacarde.domain.model.QualityInspector;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QualityInspectorDetailMapper extends GenericMapper<QualityInspectorDetailDto, QualityInspector> {

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	QualityInspector toEntity(QualityInspectorDetailDto dto);

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	QualityInspectorDetailDto toDto(QualityInspector entity);
}
