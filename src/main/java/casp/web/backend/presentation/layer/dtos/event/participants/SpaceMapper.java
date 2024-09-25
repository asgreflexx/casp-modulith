package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.Space;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpaceMapper extends BaseParticipantMapper<Space, SpaceDto> {
    SpaceMapper SPACE_MAPPER = Mappers.getMapper(SpaceMapper.class);
}
