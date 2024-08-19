package casp.web.backend.data.access.layer.documents.dog;

import casp.web.backend.data.access.layer.documents.enumerations.GradeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

// Cannot be a record because of querydsl
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

    public void setName(final String name) {
        this.name = name;
    }

    public GradeType getType() {
        return type;
    }

    public void setType(final GradeType type) {
        this.type = type;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(final long points) {
        this.points = points;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(final LocalDate examDate) {
        this.examDate = examDate;
    }
}
