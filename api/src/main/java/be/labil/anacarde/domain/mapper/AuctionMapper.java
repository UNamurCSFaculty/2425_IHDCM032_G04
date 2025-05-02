package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.AuctionDto;
import be.labil.anacarde.domain.model.Auction;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, AuctionStrategyMapper.class,
		ProductMapper.class, AuctionOptionValueMapper.class})
public abstract class AuctionMapper {

	@Mapping(source = "strategy", target = "strategy")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "auctionOptionValues", target = "auctionOptionValues")
	public abstract Auction toEntity(AuctionDto dto);

	@Mapping(source = "strategy", target = "strategy")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "auctionOptionValues", target = "auctionOptionValues")
	public abstract AuctionDto toDto(Auction auction);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Auction partialUpdate(AuctionDto dto, @MappingTarget Auction entity);
}
