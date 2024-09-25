package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.presentation.layer.dtos.event.calendar.CalendarDto;
import casp.web.backend.presentation.layer.dtos.event.participants.EventParticipantDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDto extends Event implements BaseEventDto<EventParticipantDto> {
    private List<CalendarDto> calendarEntries = new ArrayList<>();

    private Set<EventParticipantDto> participants = new HashSet<>();

    @Override
    public List<CalendarDto> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(final List<CalendarDto> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    @Override
    public Set<EventParticipantDto> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(final Set<EventParticipantDto> participants) {
        this.participants = participants;
    }
}
