package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.AuctionOptionValueDto;
import be.labil.anacarde.domain.model.AuctionOptionValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AuctionMapper.class, AuctionOptionMapper.class})
public interface AuctionOptionValueMapper extends GenericMapper<AuctionOptionValueDto, AuctionOptionValue> {

	@Override
	@Mapping(source = "auction", target = "auction")
	@Mapping(source = "auctionOption", target = "auctionOption")
	AuctionOptionValue toEntity(AuctionOptionValueDto dto);

	@Override
	@Mapping(source = "auction", target = "auction")
	@Mapping(source = "auctionOption", target = "auctionOption")
	AuctionOptionValueDto toDto(AuctionOptionValue entity);
}
