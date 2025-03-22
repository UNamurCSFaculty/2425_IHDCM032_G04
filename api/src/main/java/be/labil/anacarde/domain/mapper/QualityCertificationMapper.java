package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.QualityCertificationDto;
import be.labil.anacarde.domain.model.QualityCertification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité QualityCertification et QualityCertificationDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QualityCertificationMapper extends GenericMapper<QualityCertificationDto, QualityCertification> {

	/**
	 * Convertit une entité QualityCertification en QualityCertificationDto.
	 *
	 * @param qualityCertification
	 *            l'entité QualityCertification à convertir.
	 * @return le QualityCertificationDto correspondant.
	 */
	@Override
	@Mapping(source = "store.id", target = "storeId")
	QualityCertificationDto toDto(QualityCertification qualityCertification);

	/**
	 * Convertit un QualityCertificationDto en entité QualityCertification.
	 *
	 * @param qualityCertificationDto
	 *            le QualityCertificationDto à convertir.
	 * @return l'entité QualityCertification correspondante.
	 */
	@Override
	@Mapping(source = "storeId", target = "store.id")
	QualityCertification toEntity(QualityCertificationDto qualityCertificationDto);
}
