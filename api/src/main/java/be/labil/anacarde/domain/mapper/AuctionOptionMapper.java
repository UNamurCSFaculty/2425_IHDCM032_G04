package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.AuctionOptionDto;
import be.labil.anacarde.domain.model.AuctionOption;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuctionOptionMapper extends GenericMapper<AuctionOptionDto, AuctionOption> {

	@Override
	AuctionOption toEntity(AuctionOptionDto dto);

	@Override
	AuctionOptionDto toDto(AuctionOption entity);
}
