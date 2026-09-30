package casp.web.backend.calendar.data;

import casp.web.backend.calendar.CourseRequiredFields;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import com.querydsl.core.annotations.QueryEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@QueryEntity
@Document
public class Course extends BaseEvent<Space> implements CourseRequiredFields {

    private int spaceLimit;

    private Set<CoTrainer> coTrainers = new HashSet<>();

    public Course() {
        super(BaseEventType.COURSE);
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

    void addSpace(Space space) {
        participants.add(space);
    }

    @Override
    Set<Space> getNotDeletedParticipants() {
        return participants.stream()
                .filter(s -> isDogHasHandlerNotDeleted(s.getDogHasHandler()))
                .collect(Collectors.toSet());
    }

    private Set<CoTrainer> getNotDeletedCoTrainers() {
        return coTrainers
                .stream()
                .filter(ct -> isMemberNotDeleted(ct.getMember()))
                .collect(Collectors.toSet());
    }
}
