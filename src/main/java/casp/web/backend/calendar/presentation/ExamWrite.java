package casp.web.backend.calendar.presentation;

import jakarta.validation.constraints.NotBlank;

public class ExamWrite extends BaseEventWrite {
    @NotBlank
    private String judgeName;

    public String getJudgeName() {
        return judgeName;
    }

    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
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
