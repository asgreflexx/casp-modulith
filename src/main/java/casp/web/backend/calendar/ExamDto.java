package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ExamDto extends BaseEventDto<ExamParticipant> implements ExamRequiredFields {
    private String judgeName;

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
}
