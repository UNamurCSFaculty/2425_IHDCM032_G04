package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.AuctionStrategyDto;
import be.labil.anacarde.domain.model.AuctionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuctionStrategyMapper extends GenericMapper<AuctionStrategyDto, AuctionStrategy> {

	@Override
	AuctionStrategy toEntity(AuctionStrategyDto dto);

	@Override
	AuctionStrategyDto toDto(AuctionStrategy entity);
}
