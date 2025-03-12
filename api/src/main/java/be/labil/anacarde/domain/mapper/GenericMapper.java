package be.labil.anacarde.domain.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

/**
 * @brief Generic mapper interface for converting between DTOs and entities.
 *
 * This interface defines methods for mapping between a data transfer object (DTO) and its corresponding entity.
 * It supports conversions in both directions, as well as partial updates of existing entities with DTO data.
 * Additionally, it provides methods for converting sets of DTOs and entities.
 *
 * @tparam D The type of the Data Transfer Object.
 * @tparam E The type of the Entity.
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
