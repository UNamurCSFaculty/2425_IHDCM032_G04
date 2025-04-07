package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.AuctionDto;
import be.labil.anacarde.domain.model.Auction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AuctionStrategyMapper.class, ProductMapper.class,
		AuctionOptionValueMapper.class})
public interface AuctionMapper extends GenericMapper<AuctionDto, Auction> {

	@Override
	@Mapping(source = "strategy", target = "strategy")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "auctionOptionValues", target = "auctionOptionValues")
	Auction toEntity(AuctionDto dto);

	@Override
	@Mapping(source = "strategy", target = "strategy")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "auctionOptionValues", target = "auctionOptionValues")
	AuctionDto toDto(Auction auction);
}
