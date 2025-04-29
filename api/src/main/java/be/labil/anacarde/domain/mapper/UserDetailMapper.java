package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.CooperativeDto;
import be.labil.anacarde.domain.dto.user.*;
import be.labil.anacarde.domain.model.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper pour convertir les DTOs UserDetailDto (hors Producer) en leurs entités correspondantes. Les Producteurs sont
 * mappés dans un mapper dédié (ProducerDetailMapper) pour éviter les ambiguïtés.
 */
@Mapper(componentModel = "spring", uses = {RoleMapper.class, LanguageMapper.class,
		CooperativeMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserDetailMapper implements GenericMapper<UserDetailDto, User> {

	@Autowired
	private CooperativeMapper cooperativeMapper;

	/*------------------------------------*/
	/* 1) Conversion DTO -> Entité par type */
	/*------------------------------------*/

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract Admin toEntity(AdminDetailDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract Exporter toEntity(ExporterDetailDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract Carrier toEntity(CarrierDetailDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract QualityInspector toEntity(QualityInspectorDetailDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract Transformer toEntity(TransformerDetailDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	@Mapping(source = "agriculturalIdentifier", target = "agriculturalIdentifier")
	@Mapping(target = "cooperative", ignore = true)
	public abstract Producer toEntity(ProducerDetailDto dto);

	/**
	 * Méthode générique qui délègue à la bonne surcharge selon le type concret du DTO.
	 */
	public User toEntity(UserDetailDto dto) {
		if (dto instanceof AdminDetailDto) {
			return toEntity((AdminDetailDto) dto);
		} else if (dto instanceof ExporterDetailDto) {
			return toEntity((ExporterDetailDto) dto);
		} else if (dto instanceof CarrierDetailDto) {
			return toEntity((CarrierDetailDto) dto);
		} else if (dto instanceof QualityInspectorDetailDto) {
			return toEntity((QualityInspectorDetailDto) dto);
		} else if (dto instanceof TransformerDetailDto) {
			return toEntity((TransformerDetailDto) dto);
		} else if (dto instanceof ProducerDetailDto) {
			return toEntity((ProducerDetailDto) dto);
		}
		throw new IllegalArgumentException("Type de DTO non supporté: " + dto.getClass());
	}

	/*------------------------------------*/
	/* 2) Conversion Entité -> DTO par type */
	/*------------------------------------*/

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract AdminDetailDto toDto(Admin entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract ExporterDetailDto toDto(Exporter entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract CarrierDetailDto toDto(Carrier entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract QualityInspectorDetailDto toDto(QualityInspector entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract TransformerDetailDto toDto(Transformer entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "language", target = "language")
	public abstract ProducerDetailDto toDto(Producer entity);

	/**
	 * Méthode générique qui délègue à la bonne surcharge selon le type concret de l'entité.
	 */
	public UserDetailDto toDto(User entity) {
		if (entity instanceof Admin) {
			return toDto((Admin) entity);
		} else if (entity instanceof Exporter) {
			return toDto((Exporter) entity);
		} else if (entity instanceof Carrier) {
			return toDto((Carrier) entity);
		} else if (entity instanceof QualityInspector) {
			return toDto((QualityInspector) entity);
		} else if (entity instanceof Transformer) {
			return toDto((Transformer) entity);
		} else if (entity instanceof Producer) {
			return toDto((Producer) entity);
		}
		throw new IllegalArgumentException("Type de User non supporté: " + entity.getClass());
	}

	// —————— Hooks AfterMapping ——————

	@AfterMapping
	public void linkCooperative(ProducerDetailDto dto, @MappingTarget Producer prod) {
		if (dto.getCooperative() != null) {
			prod.setCooperative(cooperativeMapper.toEntity(dto.getCooperative()));
		}
	}

	@AfterMapping
	public void linkCoopDto(Producer entity, @MappingTarget ProducerDetailDto dto) {
		if (entity.getCooperative() != null) {
			CooperativeDto cooperativeDto = cooperativeMapper.toDto(entity.getCooperative());
			dto.setCooperative(cooperativeDto);
		}
	}

	/*------------------------------------*/
	/* 3) Mise à jour partielle (partialUpdate) */
	/*------------------------------------*/

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	public abstract void partialUpdate(AdminDetailDto dto, @MappingTarget Admin entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	public abstract void partialUpdate(ExporterDetailDto dto, @MappingTarget Exporter entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	public abstract void partialUpdate(CarrierDetailDto dto, @MappingTarget Carrier entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	public abstract void partialUpdate(QualityInspectorDetailDto dto, @MappingTarget QualityInspector entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	public abstract void partialUpdate(TransformerDetailDto dto, @MappingTarget Transformer entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "cooperative", ignore = true)
	public abstract void partialUpdate(ProducerDetailDto dto, @MappingTarget Producer entity);

	/**
	 * Délégue la partialUpdate à la bonne méthode en fonction du type.
	 */
	public User partialUpdate(UserDetailDto dto, @MappingTarget User entity) {
		if (dto instanceof AdminDetailDto && entity instanceof Admin) {
			partialUpdate((AdminDetailDto) dto, (Admin) entity);
		} else if (dto instanceof ExporterDetailDto && entity instanceof Exporter) {
			partialUpdate((ExporterDetailDto) dto, (Exporter) entity);
		} else if (dto instanceof CarrierDetailDto && entity instanceof Carrier) {
			partialUpdate((CarrierDetailDto) dto, (Carrier) entity);
		} else if (dto instanceof QualityInspectorDetailDto && entity instanceof QualityInspector) {
			partialUpdate((QualityInspectorDetailDto) dto, (QualityInspector) entity);
		} else if (dto instanceof TransformerDetailDto && entity instanceof Transformer) {
			partialUpdate((TransformerDetailDto) dto, (Transformer) entity);
		} else if (dto instanceof ProducerDetailDto && entity instanceof Producer) {
			partialUpdate((ProducerDetailDto) dto, (Producer) entity);
		} else {
			throw new IllegalArgumentException("Type incompatible pour partialUpdate: " + dto.getClass());
		}
		return entity;
	}
}
