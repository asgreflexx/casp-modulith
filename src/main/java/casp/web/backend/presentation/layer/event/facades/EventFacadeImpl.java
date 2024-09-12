package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.presentation.layer.dtos.event.types.EventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.event.participants.EventParticipantMapper.EVENT_PARTICIPANT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.EventMapper.EVENT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.member.MemberMapper.MEMBER_MAPPER;

@Service
class EventFacadeImpl implements EventFacade {
    private final EventParticipantService eventParticipantService;

    @Autowired
    EventFacadeImpl(final EventParticipantService eventParticipantService) {
        this.eventParticipantService = eventParticipantService;
    }

    @Override
    public EventDto mapBaseEventToDto(final BaseEvent baseEvent) {
        var eventDto = EVENT_MAPPER.toDto((Event) baseEvent);
        setEventParticipants(eventDto);
        return eventDto;
    }

    private void setEventParticipants(final EventDto eventDto) {
        var coTrainerDtoSet = eventParticipantService.getActiveEventParticipantsIfMembersAreActive(eventDto.getId())
                .stream()
                .map(pm -> {
                    var participantDto = EVENT_PARTICIPANT_MAPPER.toDto((EventParticipant) pm.participant());
                    participantDto.setMember(MEMBER_MAPPER.toDto(pm.member()));
                    participantDto.setBaseEvent(null);
                    return participantDto;
                }).collect(Collectors.toSet());

        eventDto.setParticipants(coTrainerDtoSet);
    }
}
