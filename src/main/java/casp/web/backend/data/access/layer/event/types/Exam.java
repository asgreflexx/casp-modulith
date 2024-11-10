package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.business.logic.layer.event.types.ExamRequiredFields;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@QueryEntity
@Document
public class Exam extends BaseEvent implements ExamRequiredFields {
    private String judgeName;

    private Set<ExamParticipant> participants = new HashSet<>();

    public Exam() {
        super(BaseEventType.EXAM);
    }

    @Override
    public String getJudgeName() {
        return judgeName;
    }

    @Override
    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
    }

    @Override
    public Set<ExamParticipant> getParticipants() {
        return participants
                .stream()
                .filter(p -> isDogHasHandlerNotDeleted(p.getDogHasHandler()))
                .collect(Collectors.toSet());
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
