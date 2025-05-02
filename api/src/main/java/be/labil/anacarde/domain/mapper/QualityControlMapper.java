package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.QualityControlDto;
import be.labil.anacarde.domain.model.QualityControl;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, UserDetailMapper.class, ProductMapper.class,
		QualityMapper.class, DocumentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class QualityControlMapper {

	@Mapping(source = "qualityInspector", target = "qualityInspector")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "document", target = "document")
	public abstract QualityControlDto toDto(QualityControl entity);

	@Mapping(source = "qualityInspector", target = "qualityInspector")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "document", target = "document")
	public abstract QualityControl toEntity(QualityControlDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "qualityInspector", target = "qualityInspector")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "quality", target = "quality")
	@Mapping(source = "document", target = "document")
	public abstract QualityControl partialUpdate(QualityControlDto dto, @MappingTarget QualityControl entity);
}
