package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.Space;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpaceMapper extends BaseParticipantMapper<Space, SpaceDto> {
    SpaceMapper SPACE_MAPPER = Mappers.getMapper(SpaceMapper.class);

    @Mapping(target = "course", source = "document.baseEvent")
    SpaceReadDto toReadDto(Space document);

    @Mapping(target = "id", conditionExpression = "java(null != dto.getId())")
    @Mapping(target = "version", conditionExpression = "java(0 < dto.getVersion())")
    @Mapping(target = "created", conditionExpression = "java(null != dto.getCreated())")
    @Mapping(target = "modified", conditionExpression = "java(null != dto.getModified())")
    Space fromWriteDto(SpaceWriteDto dto);
}
