package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.documents.event.participants.Space;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpaceMapper extends BaseMapper<Space, SpaceDto> {
    SpaceMapper SPACE_MAPPER = Mappers.getMapper(SpaceMapper.class);
}
