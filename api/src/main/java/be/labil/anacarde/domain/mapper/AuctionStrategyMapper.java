package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.AuctionStrategyDto;
import be.labil.anacarde.domain.model.AuctionStrategy;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class AuctionStrategyMapper {

	public abstract AuctionStrategy toEntity(AuctionStrategyDto dto);

	public abstract AuctionStrategyDto toDto(AuctionStrategy entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract AuctionStrategy partialUpdate(AuctionStrategyDto dto, @MappingTarget AuctionStrategy entity);
}
