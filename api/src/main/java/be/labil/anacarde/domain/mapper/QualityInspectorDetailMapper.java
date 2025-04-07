package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.QualityInspectorDetailDto;
import be.labil.anacarde.domain.model.QualityInspector;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QualityInspectorDetailMapper extends GenericMapper<QualityInspectorDetailDto, QualityInspector> {

	@Override
	QualityInspector toEntity(QualityInspectorDetailDto dto);

	@Override
	QualityInspectorDetailDto toDto(QualityInspector entity);
}
