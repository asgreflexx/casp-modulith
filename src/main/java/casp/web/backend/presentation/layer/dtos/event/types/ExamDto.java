package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.event.calendar.CalendarDto;
import casp.web.backend.presentation.layer.dtos.event.participants.ExamParticipantDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExamDto extends Exam implements BaseEventDto<ExamParticipantDto> {
    private List<CalendarDto> calendarEntries = new ArrayList<>();

    private Set<ExamParticipantDto> participants = new HashSet<>();

    @Override
    public List<CalendarDto> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(List<CalendarDto> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    @Override
    public Set<ExamParticipantDto> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(Set<ExamParticipantDto> participants) {
        this.participants = participants;
    }
}
