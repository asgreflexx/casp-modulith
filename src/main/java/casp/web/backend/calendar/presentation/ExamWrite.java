package casp.web.backend.calendar.presentation;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Setter
@Getter
public class ExamWrite extends BaseEventWrite {
    @NotBlank
    private String judgeName;
}
