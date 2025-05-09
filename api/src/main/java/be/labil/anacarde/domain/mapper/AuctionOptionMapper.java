package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.AuctionOptionDto;
import be.labil.anacarde.domain.model.AuctionOption;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AuctionOptionMapper {

	public abstract AuctionOption toEntity(AuctionOptionDto dto);

	public abstract AuctionOptionDto toDto(AuctionOption entity);
}
