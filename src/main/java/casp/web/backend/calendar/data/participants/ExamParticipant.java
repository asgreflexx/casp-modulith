package casp.web.backend.calendar.data.participants;

import casp.web.backend.common.reference.DogHasHandlerReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.UUID;

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

    public DogHasHandlerReference getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(DogHasHandlerReference dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }

    @Override
    public UUID getId() {
        return dogHasHandler.getId();
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
