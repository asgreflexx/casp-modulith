package casp.web.backend.calendar.data.participants;

import casp.web.backend.common.reference.DogHasHandlerReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Setter
@Getter
public class ExamParticipant extends BaseParticipant {
    @Valid
    @NotNull
    @DBRef
    private DogHasHandlerReference dogHasHandler;

    public ExamParticipant() {
        super(BaseParticipantType.EXAM_PARTICIPANT);
    }

    public ExamParticipant(DogHasHandlerReference dogHasHandler) {
        this();
        this.dogHasHandler = dogHasHandler;
    }

    @Override
    public UUID getId() {
        return dogHasHandler.getId();
    }
}
