package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.business.logic.layer.event.types.EventService;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.presentation.layer.dtos.event.types.EventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.EventParticipantMapper.EVENT_PARTICIPANT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.EventMapper.EVENT_MAPPER;

@Service
class EventFacadeImpl implements EventFacade {
    private static final Logger LOG = LoggerFactory.getLogger(EventFacadeImpl.class);
    private final EventParticipantService eventParticipantService;
    private final CalendarService calendarService;
    private final EventService eventService;

    @Autowired
    EventFacadeImpl(final EventParticipantService eventParticipantService,
                    final CalendarService calendarService,
                    final EventService eventService) {
        this.eventParticipantService = eventParticipantService;
        this.calendarService = calendarService;
        this.eventService = eventService;
    }

    @Override
    public EventDto mapDocumentToDto(final BaseEvent baseEvent) {
        if (baseEvent instanceof Event event) {
            var eventDto = EVENT_MAPPER.toDto(event);
            setEventParticipants(eventDto);
            return eventDto;
        } else {
            var msg = "The parameter %s is not an event".formatted(baseEvent.getEventType());
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public void save(final EventDto eventDto) {
        var event = EVENT_MAPPER.toDocument(eventDto);

        calendarService.replaceCalendarEntries(event, CALENDAR_MAPPER.toDocumentList(eventDto.getCalendarEntries()));
        eventParticipantService.replaceParticipants(event, eventDto.getParticipantsIdToWrite());

        eventService.save(event);
    }

    @Override
    public EventDto getById(final UUID id) {
        var event = eventService.getById(id);
        return mapDocumentToDto(event);
    }

    @Override
    public void deleteById(final UUID id) {
        eventService.deleteById(id);
    }

    private void setEventParticipants(final EventDto eventDto) {
        var coTrainerDtoSet = eventParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(eventDto.getId())
                .stream()
                .map(EVENT_PARTICIPANT_MAPPER::toDto)
                .collect(Collectors.toSet());

        eventDto.setParticipantsToRead(coTrainerDtoSet);
    }
}
