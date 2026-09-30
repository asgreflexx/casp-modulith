package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.ExamRequiredFields;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ExamRead extends BaseEventRead<ExamParticipant> implements ExamRequiredFields {
    private String judgeName;

    ExamRead() {
        super(BaseEventType.EXAM);
    }
}
