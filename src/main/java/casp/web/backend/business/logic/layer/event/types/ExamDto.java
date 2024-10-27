package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ExamParticipantsDtoConstraint
public class ExamDto extends BaseEventDto implements ExamRequiredFields, ExamDtoRequiredFields {
    private String judgeName;
    private Set<ExamParticipant> participants = new HashSet<>();
    private Set<UUID> newParticipants = new HashSet<>();

    ExamDto() {
        super(BaseEventType.EXAM);
    }

    @Override
    public String getJudgeName() {
        return judgeName;
    }

    @Override
    public void setJudgeName(final String judgeName) {
        this.judgeName = judgeName;
    }

    @Override
    public Set<ExamParticipant> getParticipants() {
        return participants;
    }

    @Override
    public void setParticipants(final Set<ExamParticipant> participants) {
        this.participants = participants;
    }

    @Override
    public Set<UUID> getNewParticipants() {
        return newParticipants;
    }

    @Override
    public void setNewParticipants(final Set<UUID> newParticipants) {
        this.newParticipants = newParticipants;
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
