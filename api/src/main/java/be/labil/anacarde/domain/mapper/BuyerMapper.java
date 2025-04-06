package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.BuyerDto;
import be.labil.anacarde.domain.model.Buyer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre Buyer et BuyerDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BuyerMapper extends GenericMapper<BuyerDto, Buyer> {

	@Override
	@Mapping(source = "bidder.id", target = "bidderId")
	@Mapping(source = "user.id", target = "userId")
	BuyerDto toDto(Buyer buyer);

	@Override
	@Mapping(source = "bidderId", target = "bidder.id")
	@Mapping(source = "userId", target = "user.id")
	Buyer toEntity(BuyerDto buyerDto);
}