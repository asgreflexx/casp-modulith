package casp.web.backend.calendar.data.participants;

import casp.web.backend.calendar.presentation.SpaceWriteRequiredFields;
import casp.web.backend.common.reference.DogHasHandlerReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Space extends BaseParticipant implements SpaceWriteRequiredFields {
    private String note;
    private Double paidPrice;
    private LocalDate paidDate;

    @DBRef
    private DogHasHandlerReference dogHasHandler;

    public Space() {
        super(BaseParticipantType.SPACE);
    }

    public Space(DogHasHandlerReference dogHasHandler) {
        this();
        this.dogHasHandler = dogHasHandler;
    }

    @Override
    public UUID getId() {
        return dogHasHandler.getId();
    }
}
