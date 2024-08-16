package casp.web.backend.data.access.layer.entities;

import casp.web.backend.data.access.layer.enumerations.GradeType;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class Grade {

    @NotBlank
    private String name;

    private GradeType type = GradeType.BH1;

    private Long points;

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

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", points=" + points +
                ", examDate=" + examDate +
                '}';
    }
}
