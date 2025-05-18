package be.labil.anacarde.domain.mapper;
import be.labil.anacarde.domain.dto.db.AuctionOptionsDto;
import be.labil.anacarde.domain.dto.write.AuctionOptionsUpdateDto;
import be.labil.anacarde.domain.model.AuctionOptions;
import jakarta.persistence.EntityManager;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = AuctionStrategyMapper.class)
public abstract class AuctionOptionsMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(source = "strategy", target = "strategy")
	public abstract AuctionOptionsDto toDto(AuctionOptions options);

	@Mapping(target = "strategy", ignore = true)
	public abstract AuctionOptions toEntity(AuctionOptionsUpdateDto dto);

	@AfterMapping
	protected void afterToEntity(AuctionOptionsUpdateDto dto, @MappingTarget AuctionOptions opts) {
		if (dto.getStrategyId() != null) {
			opts.setStrategy(em.getReference(be.labil.anacarde.domain.model.AuctionStrategy.class,
					dto.getStrategyId()));
		}
	}
}