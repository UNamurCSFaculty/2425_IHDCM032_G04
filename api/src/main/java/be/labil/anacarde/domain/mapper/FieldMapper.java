package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.FieldDto;
import be.labil.anacarde.domain.model.Field;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité Field et FieldDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FieldMapper extends GenericMapper<FieldDto, Field> {

	/**
	 * Convertit une entité Field en FieldDto.
	 *
	 * @param field
	 *            l'entité Field à convertir.
	 * @return le FieldDto correspondant.
	 */
	@Override
	@Mapping(source = "producer.id", target = "producerId")
	FieldDto toDto(Field field);

	/**
	 * Convertit un FieldDto en entité Field.
	 *
	 * @param fieldDto
	 *            le FieldDto à convertir.
	 * @return l'entité Field correspondante.
	 */
	@Override
	@Mapping(source = "producerId", target = "producer.id")
	Field toEntity(FieldDto fieldDto);
}
