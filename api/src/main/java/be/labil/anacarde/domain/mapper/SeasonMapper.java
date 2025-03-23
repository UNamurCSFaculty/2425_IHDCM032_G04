package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.SeasonDto;
import be.labil.anacarde.domain.model.Season;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre l'entité Season et SeasonDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeasonMapper extends GenericMapper<SeasonDto, Season> {
}
