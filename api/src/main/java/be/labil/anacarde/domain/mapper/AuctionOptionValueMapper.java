package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.AuctionOptionValueDto;
import be.labil.anacarde.domain.model.AuctionOptionValue;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, AuctionMapper.class,
		AuctionOptionMapper.class})
public abstract class AuctionOptionValueMapper {

	@Mapping(source = "auctionOption", target = "auctionOption")
	public abstract AuctionOptionValue toEntity(AuctionOptionValueDto dto);

	@Mapping(source = "auctionOption", target = "auctionOption")
	public abstract AuctionOptionValueDto toDto(AuctionOptionValue entity);

	@Mapping(target = "auctionOption", source = "auctionOption")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract AuctionOptionValue partialUpdate(AuctionOptionValueDto dto,
			@MappingTarget AuctionOptionValue entity);
}
