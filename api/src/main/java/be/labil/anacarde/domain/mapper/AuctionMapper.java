package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.AuctionDto;
import be.labil.anacarde.domain.dto.write.AuctionUpdateDto;
import be.labil.anacarde.domain.model.*;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, AuctionStrategyMapper.class,
		ProductMapper.class, TraderDetailMapper.class})
public abstract class AuctionMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(target = "strategy", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "trader", ignore = true)
	@Mapping(target = "product", ignore = true)
	@Mapping(target = "bids", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Auction toEntity(AuctionUpdateDto dto);

	@Mapping(source = "strategy", target = "strategy")
	@Mapping(source = "product", target = "product")
	@Mapping(source = "trader", target = "trader")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "bids", target = "bids")
	public abstract AuctionDto toDto(Auction auction);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "strategy", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "trader", ignore = true)
	@Mapping(target = "product", ignore = true)
	@Mapping(target = "bids", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Auction partialUpdate(AuctionUpdateDto dto, @MappingTarget Auction entity);

	@AfterMapping
	protected void afterUpdateDto(AuctionUpdateDto dto, @MappingTarget Auction a) {
		if (dto.getProductId() != null) {
			a.setProduct(em.getReference(Product.class, dto.getProductId()));
		}
		if (dto.getTraderId() != null) {
			a.setTrader(em.getReference(Trader.class, dto.getTraderId()));
		}
		if (dto.getStrategyId() != null) {
			a.setStrategy(em.getReference(AuctionStrategy.class, dto.getStrategyId()));
		}
		if (dto.getStatusId() != null) {
			a.setStatus(em.getReference(TradeStatus.class, dto.getStatusId()));
		}
	}
}
