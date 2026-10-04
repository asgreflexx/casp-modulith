package casp.web.backend.dog.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@EqualsAndHashCode
@Setter
@Getter
public class Grade {
    @NotBlank
    private String name;
    @NotNull
    private GradeType type;
    @Positive
    private long points;
    @NotNull
    private LocalDate examDate;
}
