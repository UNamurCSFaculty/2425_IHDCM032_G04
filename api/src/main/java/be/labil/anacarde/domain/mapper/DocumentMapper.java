package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité Document et DocumentDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper extends GenericMapper<DocumentDto, Document> {

	/**
	 * Convertit une entité Document en DocumentDto.
	 *
	 * @param document
	 *            l'entité Document à convertir.
	 *
	 * @return le DocumentDto correspondant.
	 */
	@Override
	@Mapping(source = "user.id", target = "userId")
	DocumentDto toDto(Document document);

	/**
	 * Convertit un DocumentDto en entité Document.
	 *
	 * @param documentDto
	 *            le DocumentDto à convertir.
	 * @return l'entité Document correspondante.
	 */
	@Override
	@Mapping(source = "userId", target = "user.id")
	Document toEntity(DocumentDto documentDto);
}