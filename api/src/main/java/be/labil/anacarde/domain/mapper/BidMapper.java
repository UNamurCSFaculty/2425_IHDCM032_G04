package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.BidDto;
import be.labil.anacarde.domain.model.Bid;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, AuctionMapper.class, TraderDetailMapper.class,
		BidStatusMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BidMapper {

	@Mapping(source = "auction", target = "auction")
	@Mapping(source = "trader", target = "trader")
	@Mapping(source = "status", target = "status")
	public abstract BidDto toDto(Bid bid);

	@Mapping(source = "auction", target = "auction")
	@Mapping(source = "trader", target = "trader")
	@Mapping(source = "status", target = "status")
	public abstract Bid toEntity(BidDto dto);

	@Mapping(source = "auction", target = "auction")
	@Mapping(source = "trader", target = "trader")
	@Mapping(source = "status", target = "status")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Bid partialUpdate(BidDto dto, @MappingTarget Bid entity);
}
