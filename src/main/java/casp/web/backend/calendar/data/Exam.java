package casp.web.backend.calendar.data;

import casp.web.backend.calendar.ExamRequiredFields;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.stream.Collectors;


@QueryEntity
@Document
public class Exam extends BaseEvent<ExamParticipant> implements ExamRequiredFields {
    private String judgeName;

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

    Set<ExamParticipant> getNotDeletedParticipants() {
        return participants
                .stream()
                .filter(p -> isDogHasHandlerNotDeleted(p.getDogHasHandler()))
                .collect(Collectors.toSet());
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
