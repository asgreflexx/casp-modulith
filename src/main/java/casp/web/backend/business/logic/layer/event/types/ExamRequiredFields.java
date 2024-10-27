package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public interface ExamRequiredFields {
    @NotBlank
    String getJudgeName();

    void setJudgeName(@NotBlank String judgeName);

    @Valid
    @NotNull
    Set<ExamParticipant> getParticipants();

    void setParticipants(@Valid @NotNull Set<ExamParticipant> participants);
}
