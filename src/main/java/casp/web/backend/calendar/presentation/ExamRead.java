package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.ExamRequiredFields;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.ExamParticipant;

import java.util.Set;

public class ExamRead extends BaseEventRead implements ExamRequiredFields {
    private String judgeName;
    private Set<ExamParticipant> participants;

    ExamRead() {
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
