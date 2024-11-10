package casp.web.backend.common.dog;

import casp.web.backend.common.enums.GradeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.Objects;

public class Grade {
    @NotBlank
    private String name;

    @NotNull
    private GradeType type;

    @Positive
    private long points;

    @NotNull
    private LocalDate examDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GradeType getType() {
        return type;
    }

    public void setType(GradeType type) {
        this.type = type;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grade grade)) return false;
        return points == grade.points && Objects.equals(name, grade.name) && type == grade.type && Objects.equals(examDate, grade.examDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, points, examDate);
    }
}
