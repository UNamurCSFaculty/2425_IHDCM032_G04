package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.BidDto;
import be.labil.anacarde.domain.dto.write.BidUpdateDto;
import be.labil.anacarde.domain.model.Bid;
import be.labil.anacarde.domain.model.TradeStatus;
import be.labil.anacarde.domain.model.Trader;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, AuctionMapper.class, TraderDetailMapper.class,
		TradeStatusMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BidMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(source = "trader", target = "trader")
	@Mapping(source = "status", target = "status")
	public abstract BidDto toDto(Bid bid);

	@Mapping(target = "status", ignore = true)
	@Mapping(target = "trader", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Bid toEntity(BidUpdateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "trader", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Bid partialUpdate(BidUpdateDto dto, @MappingTarget Bid entity);

	@AfterMapping
	protected void afterUpdateDto(BidUpdateDto dto, @MappingTarget Bid bid) {
		if (dto.getTraderId() != null) {
			bid.setTrader(em.getReference(Trader.class, dto.getTraderId()));
		}
		if (dto.getStatusId() != null) {
			bid.setStatus(em.getReference(TradeStatus.class, dto.getStatusId()));
		}
	}
}
