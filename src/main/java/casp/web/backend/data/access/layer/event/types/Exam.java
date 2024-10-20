package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@QueryEntity
@Document
public class Exam extends BaseEvent {
    @NotBlank
    private String judgeName;

    @Valid
    @NotNull
    private Set<ExamParticipant> participants = new HashSet<>();

    public Exam() {
        super(BaseEventType.EXAM);
    }

    public String getJudgeName() {
        return judgeName;
    }

    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
    }

    public Set<ExamParticipant> getParticipants() {
        return participants
                .stream()
                .filter(p -> isDogHasHandlerNotDeleted(p.getDogHasHandler()))
                .collect(Collectors.toSet());
    }

    public void setParticipants(Set<ExamParticipant> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
