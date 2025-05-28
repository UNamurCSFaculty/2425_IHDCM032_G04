package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.db.user.ProducerDetailDto;
import be.labil.anacarde.domain.dto.write.user.create.ProducerCreateDto;
import be.labil.anacarde.domain.model.Cooperative;
import be.labil.anacarde.domain.model.Language;
import be.labil.anacarde.domain.model.Producer;
import be.labil.anacarde.domain.model.User;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {MapperHelpers.class, LanguageMapper.class,
		AddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProducerDetailMapper {

	@Autowired
	protected EntityManager em;

	@Mapping(target = "cooperative", ignore = true)

	@Mapping(target = "language", ignore = true)
	@Mapping(source = "address", target = "address")
	public abstract Producer toEntity(ProducerCreateDto dto);

	@Mapping(source = "cooperative", target = "cooperative")
	@Mapping(source = "language", target = "language")
	@Mapping(source = "address", target = "address")
	public abstract ProducerDetailDto toDto(Producer entity);

	@AfterMapping
	protected void afterUpdateDto(ProducerCreateDto dto, @MappingTarget User user) {
		if (dto.getLanguageId() != null) {
			user.setLanguage(em.getReference(Language.class, dto.getLanguageId()));
		}

		if (dto.getCooperativeId() != null) {
			((Producer) user)
					.setCooperative(em.getReference(Cooperative.class, dto.getCooperativeId()));
		}
	}
}
