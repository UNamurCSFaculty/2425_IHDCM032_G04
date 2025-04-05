package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.CarrierDto;
import be.labil.anacarde.domain.model.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité Carrier et CarrierDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {RoleMapper.class})
public interface CarrierMapper extends GenericMapper<CarrierDto, Carrier> {

	/**
	 * Convertit une entité Carrier en CarrierDto.
	 *
	 * @param carrier
	 *            l'entité Carrier à convertir.
	 * @return le CarrierDto correspondant.
	 */
	@Override
	@Mapping(source = "user.id", target = "userId")
	CarrierDto toDto(Carrier carrier);

	/**
	 * Convertit un CarrierDto en entité Carrier.
	 *
	 * @param carrierDto
	 *            le CarrierDto à convertir.
	 * @return l'entité Carrier correspondante.
	 */
	@Override
	@Mapping(source = "userId", target = "user.id")
	Carrier toEntity(CarrierDto carrierDto);
}