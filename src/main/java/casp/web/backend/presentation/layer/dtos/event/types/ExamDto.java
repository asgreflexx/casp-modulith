package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.event.participants.ExamParticipantDto;
import jakarta.validation.constraints.NotBlank;

public class ExamDto extends BaseEventDto<ExamParticipantDto> {
    @NotBlank
    private String judgeName;

    public ExamDto() {
        super(Exam.EVENT_TYPE);
    }

    public String getJudgeName() {
        return judgeName;
    }

    public void setJudgeName(final String judgeName) {
        this.judgeName = judgeName;
    }
}
