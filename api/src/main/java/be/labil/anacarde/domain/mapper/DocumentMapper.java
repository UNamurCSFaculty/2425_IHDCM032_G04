package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.DocumentDto;
import be.labil.anacarde.domain.dto.write.DocumentUpdateDto;
import be.labil.anacarde.domain.model.Document;
import be.labil.anacarde.domain.model.User;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper pour convertir l'entité Document et ses DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DocumentMapper {

	@Autowired
	protected EntityManager em;

	/**
	 * Création : DocumentUpdateDto → Document (nouvelle entité). - id et uploadDate sont laissés à
	 * null / gérés par JPA. - userId → User via getReference().
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	public abstract Document toEntity(DocumentUpdateDto dto);

	/**
	 * Lecture : Document → DocumentDto - user.id → userId - tous les autres champs sont nommés à
	 * l’identique, MapStruct s’en charge automatiquement.
	 */
	@Mapping(source = "user.id", target = "userId")
	public abstract DocumentDto toDto(Document document);

	/**
	 * Écrasement complet : DocumentDto → Document. Idem que toEntity, mais depuis le DTO de
	 * lecture.
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	public abstract Document toEntity(DocumentDto dto);

	/**
	 * Mise à jour partielle : n’écrase que les champs non-null du DTO.
	 */
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUserIdToUser")
	public abstract Document partialUpdate(DocumentDto dto, @MappingTarget Document entity);

	/**
	 * Transforme un identifiant en une référence JPA User (proxy).
	 */
	@Named("mapUserIdToUser")
	protected User mapUserIdToUser(Integer userId) {
		if (userId == null) {
			return null;
		}
		// ne charge pas l’utilisateur, crée un proxy qui sera résolu à l’accès
		return em.getReference(User.class, userId);
	}
}
