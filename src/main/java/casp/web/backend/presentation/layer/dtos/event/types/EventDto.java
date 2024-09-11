package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.presentation.layer.dtos.event.participants.EventParticipantDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDto extends Event implements BaseEventDto<EventParticipantDto> {
    private List<Calendar> calendarEntries = new ArrayList<>();

    private Set<EventParticipantDto> participants = new HashSet<>();

    @Override
    public List<Calendar> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(final List<Calendar> calendarEntries) {
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
