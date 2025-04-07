package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.ProducerDetailDto;
import be.labil.anacarde.domain.dto.TraderDetailDto;
import be.labil.anacarde.domain.dto.TransformerDetailDto;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.domain.model.Trader;
import be.labil.anacarde.domain.model.Transformer;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring", uses = {ProducerDetailMapper.class, TransformerDetailMapper.class})
public interface TraderDetailMapper extends GenericMapper<TraderDetailDto, Trader> {

	@Override
	Trader toEntity(TraderDetailDto dto);

	@Override
	TraderDetailDto toDto(Trader entity);

	@ObjectFactory
	default Trader createTrader(TraderDetailDto dto) {
		if (dto instanceof ProducerDetailDto) {
			return new Producer();
		} else if (dto instanceof TransformerDetailDto) {
			return new Transformer() {
			};
		}
		throw new IllegalArgumentException("Type de TraderDto non supporté : " + dto.getClass());
	}

	@ObjectFactory
	default TraderDetailDto createTrader(Trader entity) {
		if (entity instanceof Producer) {
			return new ProducerDetailDto();
		} else if (entity instanceof Transformer) {
			return new TransformerDetailDto();
		}
		throw new IllegalArgumentException("Type de Trader non supporté : " + entity.getClass());
	}
}
