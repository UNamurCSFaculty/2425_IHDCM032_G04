package be.labil.anacarde.domain.mapper;

import be.labil.anacarde.domain.dto.TransformerDto;
import be.labil.anacarde.domain.model.Transformer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Interface Mapper pour la conversion entre Transformer et TransformerDto. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransformerMapper extends GenericMapper<TransformerDto, Transformer> {

	@Override
	@Mapping(source = "seller.id", target = "sellerId")
	@Mapping(source = "buyer.id", target = "buyerId")
	TransformerDto toDto(Transformer transformer);

	@Override
	@Mapping(source = "sellerId", target = "seller.id")
	@Mapping(source = "buyerId", target = "buyer.id")
	Transformer toEntity(TransformerDto transformerDto);
}