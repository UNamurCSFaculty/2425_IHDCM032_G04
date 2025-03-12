package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.DocumentDto;
import be.labil.anacarde.domain.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * @brief Mapper interface for converting between Document entity and DocumentDto.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper extends GenericMapper<DocumentDto, Document> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    DocumentDto toDto(Document document);

    @Override
    @Mapping(source = "userId", target = "user.id")
    Document toEntity(DocumentDto documentDto);
}
