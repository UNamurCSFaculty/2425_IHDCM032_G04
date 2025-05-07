package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.TradeStatusDto;
import be.labil.anacarde.domain.model.TradeStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TradeStatusMapper {

	public abstract TradeStatusDto toDto(TradeStatus status);

	public abstract TradeStatus toEntity(TradeStatusDto dto);

	@Mapping(source = "name", target = "name")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract TradeStatus partialUpdate(TradeStatusDto statusDto, @MappingTarget TradeStatus entity);
}
