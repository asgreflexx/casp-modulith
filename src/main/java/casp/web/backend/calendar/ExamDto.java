package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ExamDto extends BaseEventDto<ExamParticipant> implements ExamRequiredFields {
    private String judgeName;

    public ExamDto() {
        super(BaseEventType.EXAM);
    }
}
