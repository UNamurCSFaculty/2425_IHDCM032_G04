package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.ContractOfferDto;
import be.labil.anacarde.domain.model.ContractOffer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TraderDetailMapper.class,
		QualityMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractOfferMapper extends GenericMapper<ContractOfferDto, ContractOffer> {

	@Override
	@Mapping(source = "seller", target = "seller")
	@Mapping(source = "buyer", target = "buyer")
	@Mapping(source = "quality", target = "quality")
	ContractOfferDto toDto(ContractOffer offer);

	@Override
	@Mapping(source = "seller", target = "seller")
	@Mapping(source = "buyer", target = "buyer")
	@Mapping(source = "quality", target = "quality")
	ContractOffer toEntity(ContractOfferDto dto);

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "seller", target = "seller")
	@Mapping(source = "buyer", target = "buyer")
	@Mapping(source = "quality", target = "quality")
	ContractOffer partialUpdate(ContractOfferDto dto, @MappingTarget ContractOffer entity);
}
