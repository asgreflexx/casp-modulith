package casp.web.backend.presentation.layer.dtos.events.types;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Exam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExamDto extends Exam implements BaseEventDto<ExamParticipant> {
    private List<Calendar> calendarEntries = new ArrayList<>();

    private Set<ExamParticipant> participants = new HashSet<>();

    @Override
    public List<Calendar> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(List<Calendar> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    @Override
    public Set<ExamParticipant> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(Set<ExamParticipant> participants) {
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
