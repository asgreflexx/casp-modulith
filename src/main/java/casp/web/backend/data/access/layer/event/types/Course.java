package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.business.logic.layer.event.types.CourseRequiredFields;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;
import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@QueryEntity
@Document
public class Course extends BaseEvent implements CourseRequiredFields {

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
        return coTrainers
                .stream()
                .filter(ct -> isMemberNotDeleted(ct.getMember()))
                .collect(Collectors.toSet());
    }

    @Override
    public void setCoTrainers(Set<CoTrainer> coTrainers) {
        this.coTrainers = coTrainers;
    }

    @Override
    public Set<Space> getSpaces() {
        return spaces
                .stream()
                .filter(s -> isDogHasHandlerNotDeleted(s.getDogHasHandler()))
                .collect(Collectors.toSet());
    }

    @Override
    public void setSpaces(Set<Space> spaces) {
        this.spaces = spaces;
    }

    @Override
    public int getSpaceListSize() {
        return getSpaces().size();
    }

    public void addSpace(Space space) {
        spaces.add(space);
    }

    public void removeSpace(Space space) {
        spaces.remove(space);
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
