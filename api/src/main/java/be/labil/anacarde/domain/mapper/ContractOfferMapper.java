package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.ContractOfferDto;
import be.labil.anacarde.domain.model.ContractOffer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {HibernateLazyCondition.class, TraderDetailMapper.class,
		QualityMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ContractOfferMapper {

	@Mapping(source = "seller", target = "seller")
	@Mapping(source = "buyer", target = "buyer")
	@Mapping(source = "quality", target = "quality")
	public abstract ContractOfferDto toDto(ContractOffer offer);

	@Mapping(source = "seller", target = "seller")
	@Mapping(source = "buyer", target = "buyer")
	@Mapping(source = "quality", target = "quality")
	public abstract ContractOffer toEntity(ContractOfferDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "seller", target = "seller")
	@Mapping(source = "buyer", target = "buyer")
	@Mapping(source = "quality", target = "quality")
	public abstract ContractOffer partialUpdate(ContractOfferDto dto, @MappingTarget ContractOffer entity);
}
