package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.DocumentDto;
import be.labil.anacarde.domain.dto.write.DocumentUpdateDto;
import be.labil.anacarde.domain.model.Carrier;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.User;
import org.mapstruct.*;

/** Interface Mapper pour la conversion entre l'entité Document et DocumentDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DocumentMapper {

	/**
	 * Convertit une entité DocumentUpdateDto en DocumentDto.
	 */
	@Mapping(target = "id", ignore = true)
	public abstract DocumentDto toDocumentDto(DocumentUpdateDto document);

	/**
	 * Convertit un DocumentUpdateDto en entité Document.
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	public abstract Document fromDocumentUpdate(DocumentUpdateDto document);

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

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	@Mapping(source = "documentType", target = "type")
	@Mapping(target = "id", ignore = true)
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
