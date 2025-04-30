package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.AuctionOptionValueDto;
import be.labil.anacarde.domain.model.AuctionOptionValue;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AuctionMapper.class, AuctionOptionMapper.class})
public interface AuctionOptionValueMapper extends GenericMapper<AuctionOptionValueDto, AuctionOptionValue> {

	@Override
	@Mapping(source = "auctionOption", target = "auctionOption")
	AuctionOptionValue toEntity(AuctionOptionValueDto dto);

	@Override
	@Mapping(source = "auctionOption", target = "auctionOption")
	AuctionOptionValueDto toDto(AuctionOptionValue entity);

	@Override
	@Mapping(target = "auctionOption", source = "auctionOption")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	AuctionOptionValue partialUpdate(AuctionOptionValueDto dto, @MappingTarget AuctionOptionValue entity);
}
