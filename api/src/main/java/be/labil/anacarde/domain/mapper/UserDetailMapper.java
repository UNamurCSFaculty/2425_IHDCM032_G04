package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.user.*;
import be.labil.anacarde.domain.dto.write.user.*;
import be.labil.anacarde.domain.model.*;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper pour convertir les DTOs UserDetailDto (hors Producer) en leurs entités correspondantes.
 * Les Producteurs sont mappés dans un mapper dédié (ProducerDetailMapper) pour éviter les
 * ambiguïtés.
 */
@Mapper(componentModel = "spring", uses = {MapperHelpers.class, MapperHelpers.class,
		RoleMapper.class, LanguageMapper.class, AddressMapper.class,
		CooperativeMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserDetailMapper {

	@Autowired
	protected EntityManager em;

	/*------------------------------------*/
	/* Conversion DTO -> Entité par type */
	/*------------------------------------*/

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(target = "language", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Admin toEntity(AdminUpdateDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(target = "language", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Exporter toEntity(ExporterUpdateDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(target = "language", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Carrier toEntity(CarrierUpdateDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(target = "language", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract QualityInspector toEntity(QualityInspectorUpdateDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(target = "language", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Transformer toEntity(TransformerUpdateDto dto);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(target = "language", ignore = true)
	@Mapping(source = "agriculturalIdentifier", target = "agriculturalIdentifier")
	@Mapping(target = "cooperative", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Producer toEntity(ProducerUpdateDto dto);

	@AfterMapping
	protected void afterUpdateDto(UserUpdateDto dto, @MappingTarget User user) {
		if (dto.getLanguageId() != null) {
			user.setLanguage(em.getReference(Language.class, dto.getLanguageId()));
		}
		if (dto instanceof ProducerUpdateDto producer) {
			if (producer.getCooperativeId() != null) {
				((Producer) user).setCooperative(
						em.getReference(Cooperative.class, producer.getCooperativeId()));
			}
		}
	}

	/**
	 * Méthode générique qui délègue à la bonne surcharge selon le type concret du DTO.
	 */
	public User toEntity(UserUpdateDto dto) {
		if (dto instanceof AdminUpdateDto) {
			return toEntity((AdminUpdateDto) dto);
		} else if (dto instanceof ExporterUpdateDto) {
			return toEntity((ExporterUpdateDto) dto);
		} else if (dto instanceof CarrierUpdateDto) {
			return toEntity((CarrierUpdateDto) dto);
		} else if (dto instanceof QualityInspectorUpdateDto) {
			return toEntity((QualityInspectorUpdateDto) dto);
		} else if (dto instanceof TransformerUpdateDto) {
			return toEntity((TransformerUpdateDto) dto);
		} else if (dto instanceof ProducerUpdateDto) {
			return toEntity((ProducerUpdateDto) dto);
		}
		throw new IllegalArgumentException("Type de DTO non supporté: " + dto.getClass());
	}

	/*------------------------------------*/
	/* Conversion Entité -> DTO par type */
	/*------------------------------------*/

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "language", target = "language")
	public abstract AdminDetailDto toDto(Admin entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "language", target = "language")
	public abstract ExporterDetailDto toDto(Exporter entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "language", target = "language")
	public abstract CarrierDetailDto toDto(Carrier entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "language", target = "language")
	public abstract QualityInspectorDetailDto toDto(QualityInspector entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "language", target = "language")
	public abstract TransformerDetailDto toDto(Transformer entity);

	@Mapping(source = "roles", target = "roles")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "language", target = "language")
	@Mapping(source = "cooperative", target = "cooperative")
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

	/*------------------------------------------*/
	/* 3) Mise à jour partielle (partialUpdate) */
	/*------------------------------------------*/

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "address", target = "address")
	public abstract void partialUpdate(AdminUpdateDto dto, @MappingTarget Admin entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "address", target = "address")
	public abstract void partialUpdate(ExporterUpdateDto dto, @MappingTarget Exporter entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "address", target = "address")
	public abstract void partialUpdate(CarrierUpdateDto dto, @MappingTarget Carrier entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "address", target = "address")
	public abstract void partialUpdate(QualityInspectorUpdateDto dto,
			@MappingTarget QualityInspector entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "address", target = "address")
	public abstract void partialUpdate(TransformerUpdateDto dto, @MappingTarget Transformer entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "cooperative", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "address", target = "address")
	public abstract void partialUpdate(ProducerUpdateDto dto, @MappingTarget Producer entity);

	/**
	 * Délégue la partialUpdate à la bonne méthode en fonction du type.
	 */
	public User partialUpdate(UserUpdateDto dto, @MappingTarget User entity) {
		if (dto instanceof AdminUpdateDto && entity instanceof Admin) {
			partialUpdate((AdminUpdateDto) dto, (Admin) entity);
		} else if (dto instanceof ExporterUpdateDto && entity instanceof Exporter) {
			partialUpdate((ExporterUpdateDto) dto, (Exporter) entity);
		} else if (dto instanceof CarrierUpdateDto && entity instanceof Carrier) {
			partialUpdate((CarrierUpdateDto) dto, (Carrier) entity);
		} else if (dto instanceof QualityInspectorUpdateDto && entity instanceof QualityInspector) {
			partialUpdate((QualityInspectorUpdateDto) dto, (QualityInspector) entity);
		} else if (dto instanceof TransformerUpdateDto && entity instanceof Transformer) {
			partialUpdate((TransformerUpdateDto) dto, (Transformer) entity);
		} else if (dto instanceof ProducerUpdateDto && entity instanceof Producer) {
			partialUpdate((ProducerUpdateDto) dto, (Producer) entity);
		} else {
			throw new IllegalArgumentException(
					"Type incompatible pour partialUpdate: " + dto.getClass());
		}
		return entity;
	}
}
