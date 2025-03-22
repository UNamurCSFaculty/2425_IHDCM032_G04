package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité DocumentQuality et DocumentQualityDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper extends GenericMapper<DocumentDto, Document> {

	/**
	 * Convertit une entité DocumentQuality en DocumentQualityDto.
	 *
	 * @param document
	 *            l'entité DocumentQuality à convertir.
	 * @return le DocumentQualityDto correspondant.
	 */
	@Override
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "qualityCertification.id", target = "qualityCertificationId")
	DocumentDto toDto(Document document);

	/**
	 * Convertit un DocumentQualityDto en entité DocumentQuality.
	 *
	 * @param documentDto
	 *            le DocumentQualityDto à convertir.
	 * @return l'entité DocumentQuality correspondante.
	 */
	@Override
	@Mapping(source = "userId", target = "user.id")
	@Mapping(source = "qualityCertificationId", target = "qualityCertification.id")
	Document toEntity(DocumentDto documentDto);
}
