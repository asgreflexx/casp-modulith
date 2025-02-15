package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.ExamParticipant;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExamDto extends BaseEventDto implements ExamDtoRequiredFields {
    private String judgeName;
    private Set<ExamParticipant> participants = new HashSet<>();
    private Set<UUID> newParticipants = new HashSet<>();

    public ExamDto() {
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
    public Set<UUID> getNewParticipants() {
        return newParticipants;
    }

    @Override
    public void setNewParticipants(Set<UUID> newParticipants) {
        this.newParticipants = newParticipants;
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
