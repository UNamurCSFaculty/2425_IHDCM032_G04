package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.ProducerDto;
import be.labil.anacarde.domain.model.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité Producer et ProducerDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProducerMapper extends GenericMapper<ProducerDto, Producer> {

	/**
	 * Convertit une entité Producer en ProducerDto.
	 *
	 * @param producer
	 *            l'entité Producer à convertir.
	 * @return le ProducerDto correspondant.
	 */
	@Override
	@Mapping(source = "cooperative.id", target = "cooperativeId")
	@Mapping(source = "bidder.id", target = "bidderId")
	ProducerDto toDto(Producer producer);

	/**
	 * Convertit un ProducerDto en entité Producer.
	 *
	 * @param producerDto
	 *            le ProducerDto à convertir.
	 * @return l'entité Producer correspondante.
	 */
	@Override
	@Mapping(source = "cooperativeId", target = "cooperative.id")
	@Mapping(source = "bidderId", target = "bidder.id")
	Producer toEntity(ProducerDto producerDto);
}
