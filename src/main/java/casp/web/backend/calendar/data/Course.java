package casp.web.backend.calendar.data;

import casp.web.backend.calendar.BaseEventRequiredFields;
import casp.web.backend.calendar.CourseRequiredFields;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@QueryEntity
@Document
public class Course extends BaseEvent implements BaseEventRequiredFields, CourseRequiredFields {

    private int spaceLimit;

    private Set<CoTrainer> coTrainers = new HashSet<>();

    private Set<Space> spaces = new HashSet<>();

    public Course() {
        super(BaseEventType.COURSE);
    }

    @Override
    public int getSpaceLimit() {
        return spaceLimit;
    }

    @Override
    public void setSpaceLimit(int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }

    @Override
    public Set<CoTrainer> getCoTrainers() {
        return getNotDeletedCoTrainers();
    }

    @Override
    public void setCoTrainers(Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
    }

    public void addCoTrainers(Set<CoTrainer> coTrainers) {
        var notDeletedCoTrainers = getNotDeletedCoTrainers();
        notDeletedCoTrainers.addAll(coTrainers);
        this.coTrainers = notDeletedCoTrainers;
    }

    @Override
    public Set<Space> getSpaces() {
        return getNotDeletedSpaces();
    }

    @Override
    public void setSpaces(Set<Space> spaces) {
        this.spaces = spaces;
    }

    public void addSpace(Space space) {
        spaces.add(space);
    }

    public void removeSpace(Space space) {
        spaces.remove(space);
    }

    public void addSpaces(Set<Space> spaces) {
        var notDeletedSpaces = getNotDeletedSpaces();
        notDeletedSpaces.addAll(spaces);
        this.spaces = notDeletedSpaces;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private Set<CoTrainer> getNotDeletedCoTrainers() {
        return coTrainers
                .stream()
                .filter(ct -> isMemberNotDeleted(ct.getMember()))
                .collect(Collectors.toSet());
    }

    private Set<Space> getNotDeletedSpaces() {
        return spaces
                .stream()
                .filter(s -> isDogHasHandlerNotDeleted(s.getDogHasHandler()))
                .collect(Collectors.toSet());
    }
}
