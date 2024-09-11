package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDto extends Event implements BaseEventDto<EventParticipant> {
    private List<Calendar> calendarEntries = new ArrayList<>();

    private Set<EventParticipant> participants = new HashSet<>();

    @Override
    public List<Calendar> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(final List<Calendar> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    @Override
    public Set<EventParticipant> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(final Set<EventParticipant> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
