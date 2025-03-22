package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.QualityDocumentDto;
import be.labil.anacarde.domain.model.QualityDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité DocumentQuality et DocumentQualityDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QualityDocumentMapper extends GenericMapper<QualityDocumentDto, QualityDocument> {

	/**
	 * Convertit une entité DocumentQuality en DocumentQualityDto.
	 *
	 * @param qualityDocument
	 *            l'entité DocumentQuality à convertir.
	 * @return le DocumentQualityDto correspondant.
	 */
	@Override
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "qualityCertification.id", target = "qualityCertificationId")
	QualityDocumentDto toDto(QualityDocument qualityDocument);

	/**
	 * Convertit un DocumentQualityDto en entité DocumentQuality.
	 *
	 * @param qualityDocumentDto
	 *            le DocumentQualityDto à convertir.
	 * @return l'entité DocumentQuality correspondante.
	 */
	@Override
	@Mapping(source = "userId", target = "user.id")
	@Mapping(source = "qualityCertificationId", target = "qualityCertification.id")
	QualityDocument toEntity(QualityDocumentDto qualityDocumentDto);
}
