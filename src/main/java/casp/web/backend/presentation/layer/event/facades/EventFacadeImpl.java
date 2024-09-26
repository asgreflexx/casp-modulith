package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.presentation.layer.dtos.event.types.EventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.event.participants.EventParticipantMapper.EVENT_PARTICIPANT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.EventMapper.EVENT_MAPPER;

@Service
class EventFacadeImpl implements EventFacade {
    private final EventParticipantService eventParticipantService;

    @Autowired
    EventFacadeImpl(final EventParticipantService eventParticipantService) {
        this.eventParticipantService = eventParticipantService;
    }

    @Override
    public EventDto mapDocumentToDto(final BaseEvent baseEvent) {
        var eventDto = EVENT_MAPPER.toDto((Event) baseEvent);
        setEventParticipants(eventDto);
        return eventDto;
    }

    private void setEventParticipants(final EventDto eventDto) {
        var coTrainerDtoSet = eventParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(eventDto.getId())
                .stream()
                .map(EVENT_PARTICIPANT_MAPPER::toDto)
                .collect(Collectors.toSet());

        eventDto.setParticipants(coTrainerDtoSet);
    }
}
