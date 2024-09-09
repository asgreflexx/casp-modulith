package casp.web.backend.presentation.layer.dtos.member;

import casp.web.backend.data.access.layer.documents.member.Card;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CardMapper extends BaseMapper<Card, CardDto> {
    CardMapper CARD_MAPPER = Mappers.getMapper(CardMapper.class);
}
