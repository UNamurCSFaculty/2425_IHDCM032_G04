package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.QualityControlDto;
import be.labil.anacarde.domain.model.QualityControl;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {QualityInspectorDetailMapper.class, ProductMapper.class, QualityMapper.class,
		DocumentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QualityControlMapper extends GenericMapper<QualityControlDto, QualityControl> {

	@Override
	@Mapping(source = "qualityInspector", target = "qualityInspector")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "document", target = "document")
	QualityControlDto toDto(QualityControl entity);

	@Override
	@Mapping(source = "qualityInspector", target = "qualityInspector")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "document", target = "document")
	QualityControl toEntity(QualityControlDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "qualityInspector", target = "qualityInspector")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "document", target = "document")
	QualityControl partialUpdate(QualityControlDto dto, @MappingTarget QualityControl entity);
}
