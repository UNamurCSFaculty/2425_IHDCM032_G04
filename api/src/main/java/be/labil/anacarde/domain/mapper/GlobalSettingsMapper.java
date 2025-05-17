package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.GlobalSettingsDto;
import be.labil.anacarde.domain.dto.write.GlobalSettingsUpdateDto;
import be.labil.anacarde.domain.model.AuctionStrategy;
import be.labil.anacarde.domain.model.GlobalSettings;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = AuctionStrategyMapper.class)
public abstract class GlobalSettingsMapper {

	@Autowired
	protected EntityManager em;

	public abstract GlobalSettingsDto toDto(GlobalSettings entity);

	@Mapping(target = "defaultStrategy", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract GlobalSettings toEntity(GlobalSettingsUpdateDto dto);

	@AfterMapping
	protected void afterToEntity(GlobalSettingsUpdateDto dto, @MappingTarget GlobalSettings gs) {
		if (dto.getDefaultStrategyId() != null) {
			AuctionStrategy ref = em.getReference(AuctionStrategy.class,
					dto.getDefaultStrategyId());
			gs.setDefaultStrategy(ref);
		}
	}
}