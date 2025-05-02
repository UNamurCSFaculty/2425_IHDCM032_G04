package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.*;

/** Interface Mapper pour la conversion entre l'entité Document et DocumentDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DocumentMapper {

	/**
	 * Convertit une entité Document en DocumentDto.
	 *
	 * @param document
	 *            l'entité Document à convertir.
	 * @return le DocumentDto correspondant.
	 */

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "type", target = "documentType")
	public abstract DocumentDto toDto(Document document);

	/**
	 * Convertit un DocumentDto en entité Document.
	 *
	 * @param documentDto
	 *            le DocumentDto à convertir.
	 * @return l'entité Document correspondante.
	 */

	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	@Mapping(source = "documentType", target = "type")
	public abstract Document toEntity(DocumentDto documentDto);

	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	@Mapping(source = "documentType", target = "type")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Document partialUpdate(DocumentDto documentDto, @MappingTarget Document entity);

	@Named("mapUserIdToUser")
	public static User mapUserIdToUser(Integer userId) {
		if (userId == null) {
			return null;
		}
		// Par défaut, on retourne une instance concrète de User, ici Carrier.
		Carrier user = new Carrier();
		user.setId(userId);
		return user;
	}
}
