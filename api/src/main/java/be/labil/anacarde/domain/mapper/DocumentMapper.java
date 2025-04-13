package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.*;

/** Interface Mapper pour la conversion entre l'entité Document et DocumentDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper extends GenericMapper<DocumentDto, Document> {

	/**
	 * Convertit une entité Document en DocumentDto.
	 *
	 * @param document
	 *            l'entité Document à convertir.
	 * @return le DocumentDto correspondant.
	 */
	@Override
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "type", target = "documentType")
	DocumentDto toDto(Document document);

	/**
	 * Convertit un DocumentDto en entité Document.
	 *
	 * @param documentDto
	 *            le DocumentDto à convertir.
	 * @return l'entité Document correspondante.
	 */
	@Override
	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	@Mapping(source = "documentType", target = "type")
	Document toEntity(DocumentDto documentDto);

	@Override
	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	@Mapping(source = "documentType", target = "type")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Document partialUpdate(DocumentDto documentDto, @MappingTarget Document entity);

	@Named("mapUserIdToUser")
	default User mapUserIdToUser(Integer userId) {
		if (userId == null) {
			return null;
		}
		// Par défaut, on retourne une instance concrète de User, ici Carrier.
		Carrier user = new Carrier();
		user.setId(userId);
		return user;
	}
}
