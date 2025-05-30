package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.ContractOfferDto;
import be.labil.anacarde.domain.dto.write.ContractOfferUpdateDto;
import be.labil.anacarde.domain.model.ContractOffer;
import be.labil.anacarde.domain.model.Quality;
import be.labil.anacarde.domain.model.Trader;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, TraderDetailMapper.class,
		QualityMapper.class, UserMiniMapper.class,}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ContractOfferMapper {

	@Autowired
	protected EntityManager em;

	public abstract ContractOfferDto toDto(ContractOffer offer);

	@Mapping(target = "seller", ignore = true)
	@Mapping(target = "buyer", ignore = true)
	@Mapping(target = "quality", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract ContractOffer toEntity(ContractOfferUpdateDto dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "seller", ignore = true)
	@Mapping(target = "buyer", ignore = true)
	@Mapping(target = "quality", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract ContractOffer partialUpdate(ContractOfferUpdateDto dto,
			@MappingTarget ContractOffer entity);

	@AfterMapping
	protected void afterUpdateDto(ContractOfferUpdateDto dto, @MappingTarget ContractOffer a) {
		if (dto.getSellerId() != null) {
			a.setSeller(em.getReference(Trader.class, dto.getSellerId()));
		}
		if (dto.getBuyerId() != null) {
			a.setBuyer(em.getReference(Trader.class, dto.getBuyerId()));
		}
		if (dto.getQualityId() != null) {
			a.setQuality(em.getReference(Quality.class, dto.getQualityId()));
		}
	}
}
