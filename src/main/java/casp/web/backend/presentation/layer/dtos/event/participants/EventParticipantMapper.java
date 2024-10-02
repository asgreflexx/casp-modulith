package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventParticipantMapper extends BaseParticipantMapper<EventParticipant, EventParticipantDto> {
    EventParticipantMapper EVENT_PARTICIPANT_MAPPER = Mappers.getMapper(EventParticipantMapper.class);
}
