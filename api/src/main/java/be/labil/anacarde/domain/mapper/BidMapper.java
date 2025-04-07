package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.BidDto;
import be.labil.anacarde.domain.model.Bid;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AuctionMapper.class, TraderDetailMapper.class,
		BidStatusMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BidMapper extends GenericMapper<BidDto, Bid> {

	@Override
	@Mapping(source = "auction", target = "auction")
	@Mapping(source = "trader", target = "trader")
	@Mapping(source = "status", target = "status")
	BidDto toDto(Bid bid);

	@Override
	@Mapping(source = "auction", target = "auction")
	@Mapping(source = "trader", target = "trader")
	@Mapping(source = "status", target = "status")
	Bid toEntity(BidDto dto);

	@Override
	@Mapping(source = "auction", target = "auction")
	@Mapping(source = "trader", target = "trader")
	@Mapping(source = "status", target = "status")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Bid partialUpdate(BidDto dto, @MappingTarget Bid entity);
}
