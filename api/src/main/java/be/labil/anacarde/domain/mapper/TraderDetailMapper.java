package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.ProducerDetailDto;
import be.labil.anacarde.domain.dto.TraderDetailDto;
import be.labil.anacarde.domain.dto.TransformerDetailDto;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.domain.model.Trader;
import be.labil.anacarde.domain.model.Transformer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {RoleMapper.class,
		LanguageMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TraderDetailMapper extends GenericMapper<TraderDetailDto, Trader> {

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	Trader toEntity(TraderDetailDto dto);

	@Override
	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
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
