package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.documents.event.participants.EventParticipant;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventParticipantMapper extends BaseMapper<EventParticipant, EventParticipantDto> {
    EventParticipantMapper EVENT_PARTICIPANT_MAPPER = Mappers.getMapper(EventParticipantMapper.class);
}
