package be.labil.anacarde.domain.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

/**
 * Cette interface définit des méthodes pour la conversion entre un Data Transfer Object (DTO) et son entité
 * correspondante. Elle prend en charge les conversions dans les deux sens, ainsi que les mises à jour partielles
 * d'entités existantes à partir des données du DTO. De plus, elle fournit des méthodes pour convertir des ensembles
 * (Set) de DTO et d'entités.
 *
 * @param <D>
 *            Le type du Data Transfer Object.
 * @param <E>
 *            Le type de l'entité.
 */
public interface GenericMapper<D, E> {

	E toEntity(D dto);

	D toDto(E entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	E partialUpdate(D dto, @MappingTarget E entity);

	Set<E> toEntity(Set<D> dtos);

	Set<D> toDto(Set<E> entities);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Set<E> partialUpdate(Set<D> dtos, @MappingTarget Set<E> entities);
}
