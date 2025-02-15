package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.ExamDtoRequiredFields;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExamWrite extends BaseEventWrite implements ExamDtoRequiredFields {
    private String judgeName;
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<ExamParticipant> participants = new HashSet<>();
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<UUID> newParticipants = new HashSet<>();

    @Override
    public Set<UUID> getNewParticipants() {
        return newParticipants;
    }

    @Override
    public void setNewParticipants(Set<UUID> newParticipants) {
        this.newParticipants = newParticipants;
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
