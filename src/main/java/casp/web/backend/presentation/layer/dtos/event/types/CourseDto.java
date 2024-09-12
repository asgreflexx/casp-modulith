package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.presentation.layer.dtos.event.participants.CoTrainerDto;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CourseDtoSpaceConstraint
public class CourseDto extends Course implements BaseEventDto<SpaceDto> {
    @Valid
    @NotNull
    private Set<CoTrainerDto> coTrainers = new HashSet<>();

    private List<Calendar> calendarEntries = new ArrayList<>();

    private Set<SpaceDto> participants = new HashSet<>();

    public Set<CoTrainerDto> getCoTrainers() {
        return coTrainers;
    }

    public void setCoTrainers(Set<CoTrainerDto> coTrainers) {
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
    public Set<SpaceDto> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(Set<SpaceDto> participants) {
        this.participants = participants;
    }
}
