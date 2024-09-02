package casp.web.backend.business.logic.layer.events.dtos;

import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.CoTrainer;
import casp.web.backend.data.access.layer.documents.event.participant.Space;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CourseDtoSpaceConstraint
public class CourseDto extends Course implements BaseEventDto<Space> {
    @Valid
    @NotNull
    private Set<CoTrainer> coTrainers = new HashSet<>();

    private List<Calendar> calendarEntries = new ArrayList<>();

    private Set<Space> participants = new HashSet<>();

    public Set<CoTrainer> getCoTrainers() {
        return coTrainers;
    }

    public void setCoTrainers(Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
    }

    @Override
    public List<Calendar> getCalendarEntries() {
        return calendarEntries;
    }

    @Override
    public void setCalendarEntries(List<Calendar> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }

    @Override
    public Set<Space> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(Set<Space> participants) {
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
