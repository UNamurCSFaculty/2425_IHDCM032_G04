package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.db.user.TraderDetailDto;
import be.labil.anacarde.domain.dto.db.user.TransformerDetailDto;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.domain.model.Trader;
import be.labil.anacarde.domain.model.Transformer;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, LanguageMapper.class,
		AddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TraderDetailMapper {

	public abstract Trader toEntity(TraderDetailDto dto);

	public abstract TraderDetailDto toDto(Trader entity);

	@ObjectFactory
	public static Trader createTrader(TraderDetailDto dto) {
		if (dto instanceof ProducerDetailDto) {
			return new Producer();
		} else if (dto instanceof TransformerDetailDto) {
			return new Transformer() {
			};
		}
		throw new IllegalArgumentException("Type de TraderDto non supporté : " + dto.getClass());
	}

	@ObjectFactory
	public static TraderDetailDto createTrader(Trader entity) {
		if (entity instanceof Producer) {
			return new ProducerDetailDto();
		} else if (entity instanceof Transformer) {
			return new TransformerDetailDto();
		}
		throw new IllegalArgumentException("Type de Trader non supporté : " + entity.getClass());
	}
}
