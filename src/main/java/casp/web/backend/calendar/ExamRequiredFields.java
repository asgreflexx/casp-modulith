package casp.web.backend.calendar;

import casp.web.backend.calendar.data.participants.ExamParticipant;
import jakarta.validation.constraints.NotBlank;

public interface ExamRequiredFields extends BaseEventRequiredFields<ExamParticipant> {
    @NotBlank
    String getJudgeName();

    void setJudgeName(@NotBlank String judgeName);
}
